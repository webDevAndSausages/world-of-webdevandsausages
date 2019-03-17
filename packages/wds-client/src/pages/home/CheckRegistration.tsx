import React, { useReducer, useEffect } from 'react'
import { Prompt, Action, OnCmd } from '../../components/terminal'
import { useApi, endpoints } from '../../hooks/useApi'
import { ApiRequest } from '../../models/ApiRequest'
import { RegistrationModification } from '../../models/RegistrationModification'
import { Grid, Cell } from '../../components/layout'
import { LoadingEllipsis } from '../../components/LoadingEllipsis'
import { split, compose, join, pathOr, pathEq } from 'ramda'
import {
  RegistrationLabel,
  FormButton,
  FormActionButtons
} from './Registration'
import { Form, registrationReducer, defaultState } from './CancelRegistration'

import { MetaWrapper, Pre } from '../../components/DevTools'

export const CheckRegistration = ({
  eventId,
  onCommand
}: {
  eventId: number
  onCommand: OnCmd
}) => {
  const [checkState, dispatch] = useReducer(registrationReducer, defaultState)

  const updateValue = (e: React.ChangeEvent<HTMLInputElement>) =>
    dispatch({
      type: 'set',
      payload: { ...checkState.value, [e.target.id]: e.target.value }
    })

  const { request, query } = useApi(endpoints.register(eventId), false, 'post')

  useEffect(() => {
    const { ready, verificationToken } = checkState.value
    if (ready && RegistrationModification.is.EnteringValid(checkState)) {
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
      if (RegistrationModification.is.EnteringValid(checkState)) {
        dispatch({ type: 'ready' })
      }
    }
  }

  return (
    <div>
      <Prompt>
        {RegistrationModification.match(checkState, {
          Entering: values => (
            <>
              <Form
                {...values}
                updateValue={updateValue}
                handleSubmit={onSubmit}
                label="CHECK REGISTRATION"
              />
              <Grid columns={10}>
                <Cell width={3}>
                  <Prompt>$ action: </Prompt>
                </Cell>
                <Cell width={7}>
                  <FormActionButtons
                    onCommand={onCommand}
                    dispatch={dispatch}
                  />
                </Cell>
              </Grid>
            </>
          ),
          EnteringValid: values => (
            <>
              <Form
                {...values}
                updateValue={updateValue}
                handleSubmit={onSubmit}
                valid
                label="CHECK REGISTRATION"
              />
              <Grid columns={10}>
                <Cell width={3}>
                  <Prompt>$ action: </Prompt>
                </Cell>
                <Cell width={7}>
                  <FormActionButtons
                    valid
                    onCommand={onCommand}
                    dispatch={dispatch}
                  />
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
                <Form
                  {...values}
                  updateValue={updateValue}
                  handleSubmit={onSubmit}
                  disabled
                  valid
                  label="CHECK REGISTRATION"
                />
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
              <Form
                {...values}
                updateValue={updateValue}
                handleSubmit={onSubmit}
                disabled
                valid
                label="CHECK REGISTRATION"
              />
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
              <Form
                {...values}
                updateValue={updateValue}
                handleSubmit={onSubmit}
                disabled
                valid
                label="CHECK REGISTRATION"
              />
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
              <Form
                {...values}
                updateValue={updateValue}
                handleSubmit={onSubmit}
                disabled
                valid
                label="CHECK REGISTRATION"
              />
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
