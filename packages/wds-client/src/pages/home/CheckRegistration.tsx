import React, { useReducer, useEffect } from 'react'
import styled from 'styled-components'
import { Prompt, Action } from '../../components/terminal'
import { useApi, endpoints } from '../../hooks/useApi'
import { ApiRequest } from '../../models/ApiRequest'
import {
  RegistrationCheck,
  RegistrationCheckType,
  FormState
} from '../../models/RegistrationCheck'
import { Grid, Cell } from '../../components/layout'
import { LoadingEllipsis } from '../../components/LoadingEllipsis'
import { split, compose, join, pathOr, pathEq } from 'ramda'
import { RegistrationInput, RegistrationLabel } from './Registration'

import { MetaWrapper, Pre } from '../../components/DevTools'

const Form = ({
  verificationToken,
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
      $ mode: <span style={{ color: '#52bdf6' }}>CHECK REGISTRATION</span>
    </Prompt>
    <Grid columns={10} style={{ paddingTop: '20px' }}>
      <Cell width={3}>
        <RegistrationLabel valid={valid}>verification token</RegistrationLabel>
      </Cell>
      <Cell width={7}>
        <RegistrationInput
          id="verificationToken"
          type="text"
          value={verificationToken}
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

const tokenRegex = /^[a-z]+-[a-z]+$/i
export const isToken = (value: string) => tokenRegex.test(value)

const updates = {
  set: (state: RegistrationCheckType, payload: FormState) => {
    const isValid =
      typeof payload.verificationToken === 'string' &&
      isToken(payload.verificationToken)
    const newState = { ...state.value, ...payload }
    if (isValid) {
      return RegistrationCheck.EnteringValid(newState)
    }
    return RegistrationCheck.Entering(newState)
  },
  ready: (state: RegistrationCheckType) =>
    RegistrationCheck.EnteringValid({ ...state.value, ready: true }),
  loading: (state: RegistrationCheckType) =>
    RegistrationCheck.Loading({ ...state.value, ready: false }),
  success: (state: RegistrationCheckType, { data }) =>
    RegistrationCheck.Success({ ...state.value, response: data }),
  failure: (
    state: RegistrationCheckType,
    payload: { error: any; status: number }
  ) =>
    RegistrationCheck.Failure({
      ...state.value,
      error: payload.error,
      status: payload.status
    }),
  reset: (_state: RegistrationCheckType) => defaultState,
  cancel: (state: RegistrationCheckType) =>
    RegistrationCheck.Cancelled(state.value)
}

const defaultState = RegistrationCheck.Entering({
  verificationToken: '',
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
  state: RegistrationCheckType,
  action: { type: ActionType; payload?: any }
) =>
  updates[action.type]
    ? updates[action.type](state, action.payload)
    : defaultState

export const CheckRegistration = ({
  eventId,
  onCommand
}: {
  eventId: number
  onCommand: (v: Action) => void
}) => {
  const [checkState, dispatch] = useReducer(registrationReducer, defaultState)

  const updateValue = (e: any) =>
    dispatch({
      type: 'set',
      payload: { ...checkState.value, [e.target.id]: e.target.value }
    })

  const { request, query } = useApi(endpoints.register(eventId), false, 'post')

  useEffect(() => {
    const { ready, verificationToken } = checkState.value
    if (ready && RegistrationCheck.is.EnteringValid(checkState)) {
      query({
        method: 'get',
        endpoint: endpoints.checkRegistration(eventId, verificationToken)
      })
      dispatch({ type: 'loading' })
    }
  }, [checkState])

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
        {RegistrationCheck.match(checkState, {
          Entering: values => <Form {...values} updateValue={updateValue} />,
          EnteringValid: values => (
            <>
              <Form {...values} updateValue={updateValue} valid />
              <Grid columns={10}>
                <Cell width={3}>
                  <Prompt>$ action: </Prompt>
                </Cell>
                <Cell width={7}>
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
                  <Cell width={3}>
                    <Prompt>$ result: </Prompt>
                  </Cell>
                  <Cell width={7} style={{ color: '#fff' }}>
                    You are {status}.{' '}
                    {place &&
                      waiting &&
                      `You are number ${place} in the waiting list.`}
                  </Cell>
                </Grid>
              </>
            )
          },
          Failure: values => (
            <>
              <Form {...values} updateValue={updateValue} disabled valid />
              <Grid columns={10}>
                <Cell width={3}>
                  <Prompt>$ result: </Prompt>
                </Cell>
                <Cell width={7} style={{ color: '#fff' }}>
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
                <Cell width={3}>
                  <Prompt>$ result: </Prompt>
                </Cell>
                <Cell width={7} style={{ color: '#fff' }}>
                  LOADING <LoadingEllipsis />
                </Cell>
              </Grid>
            </>
          ),
          Cancelled: values => (
            <>
              <Form {...values} updateValue={updateValue} disabled valid />
              <Grid columns={10}>
                <Cell width={3}>
                  <Prompt>$ result: </Prompt>
                </Cell>
                <Cell width={7} style={{ color: '#fff' }}>
                  CANCELLED
                </Cell>
              </Grid>
            </>
          )
        })}
      </Prompt>
      {/*<MetaWrapper>
        <Pre>
          <b>state:</b> {JSON.stringify(checkState, null, 2)}
        </Pre>
      </MetaWrapper>*/}
    </div>
  )
}
