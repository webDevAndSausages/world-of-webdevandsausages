import React, { useReducer, useEffect, useRef, useState } from 'react'
import styled, { css } from '../../styles/styled-components'
import { tablet, phone } from '../../styles/helpers'
import { Prompt, blink, OnCmd } from '../../components/terminal'
import { useApi, endpoints } from '../../hooks/useApi'
import { ApiRequest } from '../../models/ApiRequest'
import { Registration, RegistrationType, FormState } from '../../models/Registration'
import { Grid, FormCell } from '../../components/layout'
import darken from 'polished/lib/color/darken'
import { LoadingEllipsis } from '../../components/LoadingEllipsis'
import { split, compose, join, pathOr, pathEq } from 'ramda'
import { isEmail } from '../../helpers/validation'
import { zip, interval } from 'rxjs'
import { map } from 'rxjs/operators'

import { ascii } from './ascii'

const asciiArray = ascii.split('\n')
const LINE_DISPLAY_DELAY = 100

function AsciiSurprise() {
  const [surprise, setState] = useState('')

  useEffect(() => {
    var events$ = zip(asciiArray, interval(LINE_DISPLAY_DELAY)).pipe(
      map(val => setState(v => v + '\n' + val[0]))
    )
    const sub$ = events$.subscribe()
    return () => sub$.unsubscribe()
  }, [])

  return (
    <div
      style={{
        width: '100%',
        display: 'flex',
        justifyContent: 'center'
      }}
    >
      <pre>{surprise}</pre>
    </div>
  )
}

const ShortCell = styled(FormCell)`
  ${tablet(css`
    grid-column-end: span 3;
  `)}
  ${phone(css`
    grid-column-end: span 10;
  `)}
`

const LongCell = styled(FormCell)`
  ${tablet(
    css`
      grid-column-end: span 7;
    `
  )}
  ${phone(css`
    grid-column-end: span 10;
  `)}
`

export const SpecialMode = styled.span<{ blink?: boolean }>`
  ${({ blink: b }) =>
    b &&
    css`
      animation: 2s ${blink} 1s infinite;
    `};
  color: ${({ theme }) => theme.primaryBlue};
`

export const RegistrationInput = styled.input`
  width: 100%;
  padding: 3px;
  color: #fff;
  background: ${({ theme }) => darken(0.08, theme.secondaryBlue)};
  border: none;
  box-shadow: inset 0 0 4px hsl(0, 0%, 80%);
  width: 95%;
  padding-left: 5px;
  outline: none;
  ::placeholder {
    color: #fff;
  }
  &:-webkit-autofill {
    -webkit-text-fill-color: #fff;
    box-shadow: 0 0 0px 1000px ${({ theme }) => darken(0.08, theme.secondaryBlue)} inset;
    -webkit-box-shadow: 0 0 0px 1000px ${({ theme }) => darken(0.08, theme.secondaryBlue)} inset;
  }
`

interface FormActionButtonProps {
  dispatch: (a: { type: ActionType; payload?: { [key: string]: any } }) => void
  onCommand: OnCmd
  valid?: boolean
  btnRef?: React.Ref<any>
}

export const FormActionButtons: React.FC<FormActionButtonProps> = ({
  dispatch,
  onCommand,
  valid
}) => (
  <>
    <FormButton
      onClick={() => dispatch({ type: 'ready' })}
      disabled={!valid}
      className={!valid ? 'disabled-btn' : ''}
    >
      submit
    </FormButton>
    <FormButton
      onClick={() => {
        dispatch({ type: 'cancel' })
        onCommand({ type: 'wait' })
      }}
    >
      cancel
    </FormButton>
    <FormButton onClick={() => dispatch({ type: 'reset' })}>reset</FormButton>
  </>
)

interface RegistrationLabelProps {
  valid: boolean
  children: any
  htmlFor?: string
}

export const RegistrationLabel = ({ valid, children, htmlFor }: RegistrationLabelProps) => (
  <Prompt htmlFor={htmlFor}>
    <SpecialMode blink={!valid}> [</SpecialMode>
    {children}
    <SpecialMode blink={!valid}>]</SpecialMode>
  </Prompt>
)

interface FormProps extends FormState {
  updateValue: (e: React.ChangeEvent<HTMLInputElement>) => void
  valid?: boolean
  disabled?: boolean
  inputRef?: React.Ref<any>
}

const Form: React.FC<FormProps> = ({
  email,
  firstName,
  lastName,
  affiliation,
  updateValue,
  valid,
  disabled,
  inputRef
}) => (
  <form style={{ width: '100%', padding: '20px 0' }}>
    <Prompt>
      $ mode: <span style={{ color: '#52bdf6' }}>REGISTER</span>
    </Prompt>
    <Grid columns={10} style={{ paddingTop: '20px' }}>
      <ShortCell width={2}>
        <RegistrationLabel htmlFor="email" valid={valid}>
          email
        </RegistrationLabel>
      </ShortCell>
      <LongCell width={8}>
        <RegistrationInput
          id="email"
          type="email"
          value={email}
          onChange={updateValue}
          disabled={disabled}
          ref={inputRef}
          aria-invalid={!valid}
        />
      </LongCell>
      <ShortCell width={2}>
        <RegistrationLabel htmlFor="firstName" valid={valid}>
          first name
        </RegistrationLabel>
      </ShortCell>
      <LongCell width={8}>
        <RegistrationInput
          id="firstName"
          type="text"
          value={firstName}
          onChange={updateValue}
          disabled={disabled}
          aria-invalid={!valid}
        />
      </LongCell>
      <ShortCell width={2}>
        <RegistrationLabel htmlFor="lastName" valid={valid}>
          last name
        </RegistrationLabel>
      </ShortCell>
      <LongCell width={8}>
        <RegistrationInput
          id="lastName"
          type="text"
          value={lastName}
          onChange={updateValue}
          disabled={disabled}
          aria-invalid={!valid}
        />
      </LongCell>
      <ShortCell width={2}>
        <RegistrationLabel htmlFor="affiliation" valid={valid}>
          affiliation
        </RegistrationLabel>
      </ShortCell>
      <LongCell width={8}>
        <RegistrationInput
          id="affiliation"
          type="text"
          value={affiliation}
          onChange={updateValue}
          disabled={disabled}
          aria-invalid={!valid}
        />
      </LongCell>
    </Grid>
  </form>
)

export const FormButton = styled.button`
  margin-right: 5px;
  cursor: pointer;
  -webkit-user-drag: none;
  user-select: none;
  zoom: 1;
  &:hover:not(.disabled-btn) {
    background: ${({ theme }) => theme.primaryOrange};
    transform: scale(1.05);
    transition: 0.1s ease;
  }
  &:disabled {
    background: light-gray;
    color: ${({ theme }) => theme.subduedTexTColor};
    cursor: not-allowed;
  }
`

const updates = {
  set: (state: RegistrationType, payload: FormState) => {
    const isValid = typeof payload.email === 'string' && isEmail(payload.email)
    const newState = { ...state.value, ...payload }
    if (isValid) {
      return Registration.EnteringValid(newState)
    }
    return Registration.Entering(newState)
  },
  ready: (state: RegistrationType) => Registration.EnteringValid({ ...state.value, ready: true }),
  loading: (state: RegistrationType) => Registration.Loading({ ...state.value, ready: false }),
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

type ActionType = 'set' | 'success' | 'failure' | 'ready' | 'reset' | 'cancel' | 'loading'

const registrationReducer = (
  state: RegistrationType,
  action: { type: ActionType; payload?: any }
) => (updates[action.type] ? updates[action.type](state, action.payload) : defaultState)

export const EventRegistration = ({
  eventId,
  onCommand
}: {
  eventId: number
  onCommand: OnCmd
}) => {
  const [registrationState, dispatch] = useReducer(registrationReducer, defaultState)

  const inputRef = useRef(null)

  useEffect(() => {
    inputRef.current.focus()
  }, [])

  const updateValue = (e: React.ChangeEvent<HTMLInputElement>) =>
    dispatch({
      type: 'set',
      payload: { ...registrationState.value, [e.target.id]: e.target.value }
    })

  const { request, query } = useApi(endpoints.register(eventId), false, 'post')

  useEffect(() => {
    const { ready, email, firstName, lastName, affiliation } = registrationState.value
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
      onCommand({ type: 'wait' })
    }
    if (ApiRequest.is.NOT_OK(request)) {
      dispatch({
        type: 'failure',
        payload: { error: request.error, status: request.status }
      })
      onCommand({ type: 'wait' })
    }
  }, [request])

  return (
    <Prompt>
      {Registration.match(registrationState, {
        Entering: values => (
          <>
            <Form {...values} updateValue={updateValue} inputRef={inputRef} />
            <Grid columns={10}>
              <ShortCell width={2}>
                <Prompt>$ action: </Prompt>
              </ShortCell>
              <LongCell width={8}>
                <FormActionButtons onCommand={onCommand} dispatch={dispatch} />
              </LongCell>
            </Grid>
          </>
        ),
        EnteringValid: values => (
          <>
            <Form {...values} updateValue={updateValue} valid />
            <Grid columns={10}>
              <ShortCell width={2}>
                <Prompt>$ action: </Prompt>
              </ShortCell>
              <LongCell width={8}>
                <FormActionButtons valid onCommand={onCommand} dispatch={dispatch} />
              </LongCell>
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

          const waiting = pathEq(['response', 'status'], 'WAIT_LISTED', values)

          return (
            <>
              <Form {...values} updateValue={updateValue} disabled valid />
              <Grid columns={10}>
                <ShortCell width={2}>
                  <Prompt>$ result: </Prompt>
                </ShortCell>
                <LongCell width={8} style={{ color: '#fff' }}>
                  You are {status}.{' '}
                  {place && waiting && `You are number ${place} in the waiting list.`}
                  <RegistrationLabel valid>200</RegistrationLabel>
                </LongCell>
                <ShortCell width={2} />
                <LongCell width={8} style={{ color: '#fff' }}>
                  Your registration token:{' '}
                  <span style={{ color: '#52bdf6' }}>
                    {pathOr('MISSING', ['response', 'verificationToken'], values)}
                  </span>
                  .
                </LongCell>
                <ShortCell width={2} />
                <LongCell width={8} style={{ color: '#fff' }}>
                  Please record the verification token for later use.
                </LongCell>
              </Grid>
              {!waiting && <AsciiSurprise />}
            </>
          )
        },
        Failure: values => (
          <>
            <Form {...values} updateValue={updateValue} disabled valid />
            <Grid columns={10}>
              <ShortCell width={2}>
                <Prompt>$ result: </Prompt>
              </ShortCell>
              <LongCell width={8} style={{ color: '#fff' }}>
                {values.error.message} <RegistrationLabel valid>{values.status}</RegistrationLabel>
              </LongCell>
            </Grid>
          </>
        ),
        Loading: values => (
          <>
            <Form {...values} updateValue={updateValue} disabled valid />
            <Grid columns={10}>
              <ShortCell width={2}>
                <Prompt>$ result: </Prompt>
              </ShortCell>
              <LongCell width={8} style={{ color: '#fff' }}>
                LOADING <LoadingEllipsis />
              </LongCell>
            </Grid>
          </>
        ),
        Cancelled: values => (
          <>
            <Form {...values} updateValue={updateValue} disabled valid />
            <Grid columns={10}>
              <ShortCell width={2}>
                <Prompt>$ result: </Prompt>
              </ShortCell>
              <LongCell width={8} style={{ color: '#fff' }}>
                CANCELLED
              </LongCell>
            </Grid>
          </>
        )
      })}
    </Prompt>
  )
}
