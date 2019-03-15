import React, { useState, useReducer, useEffect } from 'react'
import styled from 'styled-components'
import { EventDetailLabel } from '../../components/terminal/TerminalDetail'
import { useApi, endpoints } from '../../hooks/useApi'
import { ApiRequest } from '../../models/ApiRequest'
import { Registration, RegistrationType } from '../../models/Registration'
import { CursorInput } from '../../components/terminal'

const Loading = styled.div`
  font-size: 24px;
  color: #cdee69;
`

const updates = {
  email: (state: RegistrationType) =>
    Registration.EnteringEmail({ prompt: 'Enter your email:', last: state }),
  name: (state: RegistrationType) =>
    Registration.EnteringName({ prompt: 'Enter your name:', last: state }),
  affiliation: (state: RegistrationType) =>
    Registration.EnteringAffiliation({
      prompt: 'Enter your affiliation (company) and press return to register:',
      last: state
    }),
  success: () => Registration.Success({ prompt: 'You are registered' }),
  failure: (state: RegistrationType) =>
    Registration.Failure({
      prompt: 'Oops, something is fucked up',
      last: state
    }),
  loading: () => Registration.Loading()
}

const defaultState = Registration.EnteringEmail({ prompt: 'Enter your email:' })

type Action =
  | 'email'
  | 'name'
  | 'affiliation'
  | 'back'
  | 'success'
  | 'failure'
  | 'loading'

const registrationReducer = (state: RegistrationType, action: Action) =>
  updates[action] ? updates[action](state) : defaultState

export const ConsoleRegistration = ({ eventId }: { eventId: number }) => {
  const [registrationState, dispatch] = useReducer(
    registrationReducer,
    defaultState
  )
  const [email, setEmail] = useState('')
  const [affiliation, setAffiliation] = useState('')
  const [name, setName] = useState('')
  const [ready, setReady] = useState(false)
  const { request, query } = useApi(endpoints.register(eventId), false, 'post')

  // TODO: validate
  useEffect(() => {
    if (ready) {
      query({
        method: 'post',
        endpoint: endpoints.register(eventId),
        payload: { email, name, affiliation }
      })
    }
  }, [ready])

  useEffect(() => {
    if (ApiRequest.is.LOADING(request)) {
      dispatch('loading')
    }
    if (ApiRequest.is.OK(request)) {
      dispatch('success')
    }
    if (ApiRequest.is.NOT_OK(request)) {
      dispatch('failure')
    }
  }, [request])

  const getAction = Registration.match({
    EnteringEmail: () => 'name',
    EnteringName: () => 'affiliation',
    EnteringAffiliation: () => {
      setReady(true)
    },
    default: () => {
      return null
    }
  })
  // TODO validate
  const dispatchCommand = (e: any) => {
    if (e.charCode == '13') {
      const act = getAction(registrationState)
      act && dispatch(act)
    }
    if (e.charCode === '27' || e.charCode === '37') {
      dispatch('back')
    }
  }

  return (
    <EventDetailLabel>
      {Registration.match(registrationState, {
        EnteringEmail: ({ prompt }) => {
          return (
            <>
              $ {prompt}
              {/*<CursorInput
                commandValue={email}
                onChange={e => setEmail(e.target.value)}
                onKeyPress={dispatchCommand}
              />*/}
            </>
          )
        },
        EnteringName: ({ prompt }) => {
          return (
            <>
              $ {prompt}
              {/*<CursorInput
                commandValue={name}
                onChange={e => setName(e.target.value)}
                onKeyPress={dispatchCommand}
              />*/}
            </>
          )
        },
        EnteringAffiliation: ({ prompt }) => {
          return (
            <>
              $ {prompt}
              {/*<CursorInput
                commandValue={name}
                onChange={e => setName(e.target.value)}
                onKeyPress={dispatchCommand}
              />*/}
            </>
          )
        },
        Success: ({ prompt }) => <>{prompt}</>,
        Failure: ({ prompt }) => <>{prompt}</>,
        Loading: () => <Loading>Hold on...</Loading>
      })}
    </EventDetailLabel>
  )
}
