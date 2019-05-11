import React, { useReducer, useEffect, useRef, useState } from 'react'
import { Prompt, OnCmd } from '../../components/terminal'
import { useApi, endpoints } from '../../hooks/useApi'
import { ApiRequest } from '../../models/ApiRequest'
import {
  RegistrationModification,
  RegistrationModificationType,
  FormState
} from '../../models/RegistrationModification'
import { Grid, FormCell } from '../../components/layout'
import { LoadingEllipsis } from '../../components/LoadingEllipsis'
import { RegistrationInput, RegistrationLabel, FormActionButtons } from './Registration'
import { isToken } from '../../helpers/validation'

interface FormProps extends FormState {
  updateValue: (e: React.ChangeEvent<HTMLInputElement>) => void
  valid?: boolean
  disabled?: boolean
  label?: string
  handleSubmit: (e: React.KeyboardEvent<HTMLDivElement>) => void
  inputRef?: React.Ref<any>
}

export const Form: React.FC<FormProps> = ({
  verificationToken,
  updateValue,
  valid,
  disabled,
  label = 'CANCEL REGISTRATION',
  handleSubmit,
  inputRef
}) => (
  <form style={{ width: '100%', padding: '20px 0' }}>
    <Prompt>
      $ mode: <span style={{ color: '#52bdf6' }}>{label}</span>
    </Prompt>
    <Grid columns={10} style={{ paddingTop: '20px' }}>
      <FormCell width={3}>
        <RegistrationLabel valid={valid}>verification token</RegistrationLabel>
      </FormCell>
      <FormCell width={7}>
        <RegistrationInput
          id="verificationToken"
          type="text"
          value={verificationToken}
          onChange={updateValue}
          disabled={disabled}
          onKeyPress={handleSubmit}
          ref={inputRef}
        />
      </FormCell>
    </Grid>
  </form>
)

export const updates = {
  set: (state: RegistrationModificationType, payload: FormState) => {
    const isValid =
      typeof payload.verificationToken === 'string' && isToken(payload.verificationToken)
    const newState = { ...state.value, ...payload }
    if (isValid) {
      return RegistrationModification.EnteringValid(newState)
    }
    return RegistrationModification.Entering(newState)
  },
  ready: (state: RegistrationModificationType) =>
    RegistrationModification.EnteringValid({ ...state.value, ready: true }),
  loading: (state: RegistrationModificationType) =>
    RegistrationModification.Loading({ ...state.value, ready: false }),
  success: (state: RegistrationModificationType, { data }) =>
    RegistrationModification.Success({ ...state.value, response: data }),
  failure: (state: RegistrationModificationType, payload: { error: any; status: number }) =>
    RegistrationModification.Failure({
      ...state.value,
      error: payload.error,
      status: payload.status
    }),
  reset: (_state: RegistrationModificationType) => defaultState,
  cancel: (state: RegistrationModificationType) => RegistrationModification.Cancelled(state.value)
}

export const defaultState = RegistrationModification.Entering({
  verificationToken: '',
  ready: false
})

export type ActionType = 'set' | 'success' | 'failure' | 'ready' | 'reset' | 'cancel' | 'loading'

export const registrationReducer = (
  state: RegistrationModificationType,
  action: { type: ActionType; payload?: any }
) => (updates[action.type] ? updates[action.type](state, action.payload) : defaultState)

export const CancelRegistration = ({
  eventId,
  onCommand,
  cancelToken
}: {
  eventId: number
  onCommand: OnCmd
  cancelToken: string | null
}) => {
  const inputRef = useRef(null)

  const [cancelState, dispatch] = useReducer(registrationReducer, defaultState)

  useEffect(() => {
    if (cancelToken) {
      dispatch({ type: 'set', payload: { verificationToken: cancelToken } })
    }
  }, [cancelToken])

  useEffect(() => {
    if (!cancelToken) {
      inputRef.current.focus()
    }
  }, [cancelToken])

  const updateValue = (e: React.ChangeEvent<HTMLInputElement>) =>
    dispatch({
      type: 'set',
      payload: { ...cancelState.value, [e.target.id]: e.target.value }
    })

  const { request, query } = useApi(endpoints.register(eventId), false, 'post')

  useEffect(() => {
    const { ready, verificationToken } = cancelState.value
    if (ready && RegistrationModification.is.EnteringValid(cancelState)) {
      query({
        method: 'delete',
        endpoint: endpoints.cancelRegistration(verificationToken)
      })
      dispatch({ type: 'loading' })
    }
  }, [cancelState])

  useEffect(() => {
    if (ApiRequest.is.OK(request)) {
      dispatch({
        type: 'success',
        payload: { data: request.data }
      })
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

  const onSubmit = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      e.stopPropagation()
      if (RegistrationModification.is.EnteringValid(cancelState)) {
        dispatch({ type: 'ready' })
      }
    }
  }

  return (
    <Prompt>
      {RegistrationModification.match(cancelState, {
        Entering: values => (
          <>
            <Form
              {...values}
              updateValue={updateValue}
              handleSubmit={onSubmit}
              inputRef={inputRef}
            />
            <Grid columns={10}>
              <FormCell width={3}>
                <Prompt>$ action: </Prompt>
              </FormCell>
              <FormCell width={7}>
                <FormActionButtons dispatch={dispatch} onCommand={onCommand} />
              </FormCell>
            </Grid>
          </>
        ),
        EnteringValid: values => (
          <>
            <Form {...values} updateValue={updateValue} valid handleSubmit={onSubmit} />
            <Grid columns={10}>
              <FormCell width={3}>
                <Prompt>$ action: </Prompt>
              </FormCell>
              <FormCell width={7}>
                <FormActionButtons valid dispatch={dispatch} onCommand={onCommand} />
              </FormCell>
            </Grid>
          </>
        ),
        Success: values => {
          return (
            <>
              <Form {...values} updateValue={updateValue} disabled valid handleSubmit={onSubmit} />
              <Grid columns={10}>
                <FormCell width={3}>
                  <Prompt>$ result: </Prompt>
                </FormCell>
                <FormCell width={7} style={{ color: '#fff' }}>
                  Your registration has been removed.{' '}
                  <RegistrationLabel valid>200</RegistrationLabel>
                </FormCell>
              </Grid>
            </>
          )
        },
        Failure: values => (
          <>
            <Form {...values} updateValue={updateValue} disabled valid handleSubmit={onSubmit} />
            <Grid columns={10}>
              <FormCell width={3}>
                <Prompt>$ result: </Prompt>
              </FormCell>
              <FormCell width={7} style={{ color: '#fff' }}>
                {values.error.message} <RegistrationLabel valid>{values.status}</RegistrationLabel>
              </FormCell>
            </Grid>
          </>
        ),
        Loading: values => (
          <>
            <Form {...values} updateValue={updateValue} disabled valid handleSubmit={onSubmit} />
            <Grid columns={10}>
              <FormCell width={3}>
                <Prompt>$ result: </Prompt>
              </FormCell>
              <FormCell width={7} style={{ color: '#fff' }}>
                LOADING <LoadingEllipsis />
              </FormCell>
            </Grid>
          </>
        ),
        Cancelled: values => (
          <>
            <Form {...values} updateValue={updateValue} disabled valid handleSubmit={onSubmit} />
            <Grid columns={10}>
              <FormCell width={3}>
                <Prompt>$ result: </Prompt>
              </FormCell>
              <FormCell width={7} style={{ color: '#fff' }}>
                CANCELLED
              </FormCell>
            </Grid>
          </>
        )
      })}
    </Prompt>
  )
}
