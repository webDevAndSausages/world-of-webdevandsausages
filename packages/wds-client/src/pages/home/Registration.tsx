import React, { useReducer, useEffect } from 'react'
import styled, { css } from 'styled-components'
import { Prompt, blink, Action } from '../../components/terminal'
import { useApi, endpoints } from '../../hooks/useApi'
import { ApiRequest } from '../../models/ApiRequest'
import {
  Registration,
  RegistrationType,
  FormState
} from '../../models/Registration'
import { Grid, Cell } from '../../components/layout'
import lighten from 'polished/lib/color/lighten'
import { LoadingEllipsis } from '../../components/LoadingEllipsis'
import { split, compose, join, pathOr, pathEq } from 'ramda'

export const SpecialMode = styled.span<{ blink?: boolean }>`
  ${({ blink: b }) =>
    b &&
    css`
      animation: 2s ${blink} 1s infinite;
    `};
  color: ${({ theme }) => theme.primaryBlue};
`

const inputColor = lighten(0.2, '#4e4e4e')

export const RegistrationInput = styled.input`
  width: 100%;
  color: #fff;
  background: ${lighten(0.2, '#4e4e4e')};
  border: none;
  box-shadow: none;
  width: 100%;
  padding-left: 5px;
  outline: none;
  ::placeholder {
    color: #fff;
  }
  &:-webkit-autofill {
    -webkit-text-fill-color: #fff;
    box-shadow: 0 0 0px 1000px ${inputColor} inset;
    -webkit-box-shadow: 0 0 0px 1000px ${inputColor} inset;
  }
`

export const RegistrationLabel = ({ valid, children }) => (
  <Prompt>
    <SpecialMode blink={!valid}> [</SpecialMode>
    {children}
    <SpecialMode blink={!valid}>]</SpecialMode>
  </Prompt>
)

const Form = ({
  email,
  firstName,
  lastName,
  affiliation,
  updateValue,
  valid,
  disabled
}: FormState & {
  updateValue: (e: any) => void
  valid?: boolean
  disabled?: boolean
}) => (
  <form style={{ width: '100%', padding: '20px 0' }}>
    <Prompt>
      $ mode: <span style={{ color: '#52bdf6' }}>REGISTER</span>
    </Prompt>
    <Grid columns={10} style={{ paddingTop: '20px' }}>
      <Cell width={2}>
        <RegistrationLabel valid={valid}>email</RegistrationLabel>
      </Cell>
      <Cell width={8}>
        <RegistrationInput
          id="email"
          type="email"
          value={email}
          onChange={updateValue}
          disabled={disabled}
        />
      </Cell>
      <Cell width={2}>
        <RegistrationLabel valid={valid}>first name</RegistrationLabel>
      </Cell>
      <Cell width={8}>
        <RegistrationInput
          id="firstName"
          type="text"
          value={firstName}
          onChange={updateValue}
          disabled={disabled}
        />
      </Cell>
      <Cell width={2}>
        <RegistrationLabel valid={valid}>last name</RegistrationLabel>
      </Cell>
      <Cell width={8}>
        <RegistrationInput
          id="lastName"
          type="text"
          value={lastName}
          onChange={updateValue}
          disabled={disabled}
        />
      </Cell>
      <Cell width={2}>
        <RegistrationLabel valid={valid}>affiliation</RegistrationLabel>
      </Cell>
      <Cell width={8}>
        <RegistrationInput
          id="affiliation"
          type="text"
          value={affiliation}
          onChange={updateValue}
          disabled={disabled}
        />
      </Cell>
    </Grid>
  </form>
)

const FormButton = styled.button`
  margin-right: 5px;
  cursor: pointer;
  -webkit-user-drag: none;
  user-select: none;
  zoom: 1;
  &:hover {
    background: ${({ theme }) => theme.primaryOrange};
    transform: scale(1.05);
    transition: 0.1s ease;
  }
`

const emailRegex = /^.+@.+\..+$/i
export const isEmail = (value: string) => emailRegex.test(value)

const updates = {
  set: (state: RegistrationType, payload: FormState) => {
    const isValid = typeof payload.email === 'string' && isEmail(payload.email)
    const newState = { ...state.value, ...payload }
    if (isValid) {
      return Registration.EnteringValid(newState)
    }
    return Registration.Entering(newState)
  },
  ready: (state: RegistrationType) =>
    Registration.EnteringValid({ ...state.value, ready: true }),
  loading: (state: RegistrationType) =>
    Registration.Loading({ ...state.value, ready: false }),
  success: (state: RegistrationType, { data }) =>
    Registration.Success({ ...state.value, response: data }),
  failure: (state: RegistrationType, payload: { error: any; status: number }) =>
    Registration.Failure({
      ...state.value,
      error: payload.error,
      status: payload.status
    }),
  reset: (_state: RegistrationType) => defaultState,
  cancel: (state: RegistrationType) => Registration.Cancelled(state.value)
}

const defaultState = Registration.Entering({
  email: '',
  firstName: '',
  lastName: '',
  affiliation: '',
  ready: false
})

type ActionType =
  | 'set'
  | 'success'
  | 'failure'
  | 'ready'
  | 'reset'
  | 'cancel'
  | 'loading'

const registrationReducer = (
  state: RegistrationType,
  action: { type: ActionType; payload?: any }
) =>
  updates[action.type]
    ? updates[action.type](state, action.payload)
    : defaultState

export const EventRegistration = ({
  eventId,
  onCommand
}: {
  eventId: number
  onCommand: (v: Action) => void
}) => {
  const [registrationState, dispatch] = useReducer(
    registrationReducer,
    defaultState
  )

  const updateValue = (e: any) =>
    dispatch({
      type: 'set',
      payload: { ...registrationState.value, [e.target.id]: e.target.value }
    })

  const { request, query } = useApi(endpoints.register(eventId), false, 'post')

  useEffect(() => {
    const {
      ready,
      email,
      firstName,
      lastName,
      affiliation
    } = registrationState.value
    if (ready && Registration.is.EnteringValid(registrationState)) {
      query({
        method: 'post',
        endpoint: endpoints.register(eventId),
        payload: { email, firstName, lastName, affiliation }
      })
      dispatch({ type: 'loading' })
    }
  }, [registrationState])

  useEffect(() => {
    if (ApiRequest.is.OK(request)) {
      dispatch({ type: 'success', payload: { data: request.data.registered } })
      onCommand('wait')
    }
    if (ApiRequest.is.NOT_OK(request)) {
      dispatch({
        type: 'failure',
        payload: { error: request.error, status: request.status }
      })
      onCommand('wait')
    }
  }, [request])

  return (
    <div>
      <Prompt>
        {Registration.match(registrationState, {
          Entering: values => <Form {...values} updateValue={updateValue} />,
          EnteringValid: values => (
            <>
              <Form {...values} updateValue={updateValue} valid />
              <Grid columns={10}>
                <Cell width={2}>
                  <Prompt>$ action: </Prompt>
                </Cell>
                <Cell width={8}>
                  <FormButton onClick={() => dispatch({ type: 'ready' })}>
                    submit
                  </FormButton>
                  <FormButton
                    onClick={() => {
                      dispatch({ type: 'cancel' })
                      onCommand('wait')
                    }}
                  >
                    cancel
                  </FormButton>
                  <FormButton onClick={() => dispatch({ type: 'reset' })}>
                    reset
                  </FormButton>
                </Cell>
              </Grid>
            </>
          ),
          Success: values => {
            const status = compose(
              join(' '),
              split('_'),
              pathOr('REGISTERED', ['response', 'status'])
            )(values)

            const place = pathOr(null, ['response', 'orderNumber'], values)

            const waiting = pathEq(
              ['response', 'status'],
              'WAIT_LISTED',
              values
            )

            return (
              <>
                <Form {...values} updateValue={updateValue} disabled valid />
                <Grid columns={10}>
                  <Cell width={2}>
                    <Prompt>$ result: </Prompt>
                  </Cell>
                  <Cell width={8} style={{ color: '#fff' }}>
                    You are {status}.{' '}
                    {place &&
                      waiting &&
                      `You are number ${place} in the waiting list.`}
                  </Cell>
                  <Cell width={2} />
                  <Cell width={8} style={{ color: '#fff' }}>
                    Your registration token:{' '}
                    <span style={{ color: '#52bdf6' }}>
                      {pathOr(
                        'MISSING',
                        ['response', 'verificationToken'],
                        values
                      )}
                    </span>
                    .
                  </Cell>
                  <Cell width={2} />
                  <Cell width={8} style={{ color: '#fff' }}>
                    Please record the verification token for later use.
                  </Cell>
                </Grid>
              </>
            )
          },
          Failure: values => (
            <>
              <Form {...values} updateValue={updateValue} disabled valid />
              <Grid columns={10}>
                <Cell width={2}>
                  <Prompt>$ result: </Prompt>
                </Cell>
                <Cell width={8} style={{ color: '#fff' }}>
                  {values.error.message}{' '}
                  <RegistrationLabel valid>{values.status}</RegistrationLabel>
                </Cell>
              </Grid>
            </>
          ),
          Loading: values => (
            <>
              <Form {...values} updateValue={updateValue} disabled valid />
              <Grid columns={10}>
                <Cell width={2}>
                  <Prompt>$ result: </Prompt>
                </Cell>
                <Cell width={8} style={{ color: '#fff' }}>
                  LOADING <LoadingEllipsis />
                </Cell>
              </Grid>
            </>
          ),
          Cancelled: values => (
            <>
              <Form {...values} updateValue={updateValue} disabled valid />
              <Grid columns={10}>
                <Cell width={2}>
                  <Prompt>$ result: </Prompt>
                </Cell>
                <Cell width={8} style={{ color: '#fff' }}>
                  CANCELLED
                </Cell>
              </Grid>
            </>
          )
        })}
      </Prompt>
      {/*<MetaWrapper>
        <Pre>
          <b>state:</b> {JSON.stringify(registrationState, null, 2)}
        </Pre>
      </MetaWrapper>*/}
    </div>
  )
}
