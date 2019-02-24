import React, { useEffect, useRef, useMemo, useState } from 'react'
import styled, { css } from '../../styles/styled-components'
import { toRem, phone, tablet } from '../../styles/helpers'
import transparentize from 'polished/lib/color/transparentize'
import { Machine } from 'xstate'
import { machineConfig } from './mailingListMachineConfig'
import { BehaviorSubject, pipe } from 'rxjs'
import { tap, map } from 'rxjs/operators'
import { identity } from 'ramda'

import Button from '../../components/Button'
import Notification from '../../components/Notification'
import { isEmail } from '../../helpers/validation'

import { theme } from '../../styles/theme'

const Input = styled.input<any>`
  border: 2px solid ${theme.secondaryBlue};
  background: ${transparentize(0.8, theme.secondaryBlue)};
  border-radius: 3px;
  color: white;
  width: 30%;
  font-size: ${toRem(20)};
  font-weight: 400;
  height: 52px;
  padding: ${toRem(10)} ${toRem(15)};
  vertical-align: middle;
  box-sizing: border-box;
  margin: 0;
  outline: 0;
  &:hover,
  &:readonly,
  &:focus {
    outline: none;
    background: #0b7ebc;
    color: white;
  }
  /* removes chrome's yellow background */
  &,
  &:-webkit-autofill,
  &:-webkit-autofill:hover,
  &:-webkit-autofill:focus,
  &:-webkit-autofill:active {
    -webkit-text-fill-color: var(--color-txt-dark);
    box-shadow: 0 0 0px 1000px var(--color-background) inset;
    -webkit-box-shadow: 0 0 0px 1000px var(--color-background) inset;
  }
  ::placeholder {
    opacity: 0.5;
  }
  ${tablet(css`
    width: 60%;
  `)};
  ${phone(css`
    width: 70%;
  `)};
  ${({ err }) =>
    err &&
    css`
      border-color: red;
    `}
  ${p =>
    p.valid &&
    css`
      background-color: ${theme.secondaryBlue};
    `};
`

const FieldWrapper = styled.div`
  display: flex;
  justify-content: center;
`

interface Errors {
  email?: string
}

const validate = values => {
  const errors: Errors = {}
  if (!values.email) {
    errors.email = 'Required'
  } else if (!isEmail(values.email)) {
    errors.email = 'Invalid format'
  }
  return errors
}

export const MetaWrapper = styled('div')`
  padding: 1rem;

  position: fixed;
  top: 100px;
  left: 0;

  display: flex;
  flex-direction: column;

  color: white;
  background: hsla(0, 0%, 0%, 0.6);
`

export const Pre = styled('pre')`
  margin-bottom: 1rem;

  font-size: 12;
  b {
    font-weight: bold;
  }
`

const TitleWrapper = styled.div`
  display: flex;
  justify-content: center;
`

const Title = styled.h2`
  color: #fff2;
  font-weight: 400;
  font-size: 20px;
  line-height: 150%;
  margin: 0;
  padding-bottom: 10px;
`

const FormWrapper = styled.div`
  padding: 20px;
`

interface Values {
  email: string
}

function useMailingListMachine(config, options, epic = pipe(map(identity))) {
  const machine = useMemo(() => Machine(config, options), [])
  let inputsRef$ = useRef(null)

  const [state, setState] = useState(machine.initialState)

  useEffect(() => {
    const machineState$ = new BehaviorSubject([
      machine.initialState,
      machine.context,
      undefined
    ])

    inputsRef$.current = machineState$

    const effect$ = epic(inputsRef$.current)

    const effectSubscription$ = effect$.subscribe({
      next: ([state, ctx, value]) => {
        const { actions } = state
        let nextContext = ctx
        actions.forEach(action => {
          // If the action is executable, execute it
          if (action.exec) {
            const result = action.exec(ctx, value)
            nextContext = { ...ctx, ...result }
          }
        })
        state.context = nextContext
        setState(state)
      }
    })
    return () => effectSubscription$.unsubscribe()
  }, [])

  // Setup the service only once.
  const service = useMemo(() => {
    function send({ type, value }: { type: string; value?: any }) {
      const nextState = machine.transition(state, type)
      inputsRef$.current.next([nextState, state.context, value])
    }
    return { send }
  }, [state.context])

  return { state, send: service.send, context: state.context }
}

export function MailingListForm() {
  const emailInputRef = useRef()
  const { state, send, context } = useMailingListMachine(
    machineConfig,
    {
      actions: {
        cacheEmail: (ctx, value) => ({ email: value })
      },
      guards: {
        isBadEmailFormat: ctx => !isEmail(ctx.email)
      }
    },
    vals => vals.pipe(tap(([a, b, c]) => console.log('epic', a, b, c)))
  )

  return (
    <FormWrapper>
      <form
        onSubmit={e => {
          e.preventDefault()
          send({ type: 'SUBMIT' })
        }}
      >
        <TitleWrapper>
          <Title>Join our mailing list to hear about upcoming events:</Title>
        </TitleWrapper>

        <FieldWrapper>
          <Input
            id="email"
            type="email"
            placeholder="charlie@gmail.com"
            onBlur={() => {
              send({ type: 'EMAIL_BLUR' })
            }}
            value={context.email}
            err={state.matches('emailErr')}
            disabled={state.matches('SUBMIT')}
            onChange={e => {
              send({
                type: 'ENTER_EMAIL',
                value: e.target.value
              })
            }}
            ref={emailInputRef}
            autoFocus
          />
          <Button
            type="submit"
            loading={state.matches('SUBMIT')}
            secondary
            disabled={state.matches('emailErr')}
            valid={true}
            white
          >
            {'+'}
          </Button>
        </FieldWrapper>
        <Notification
          type="success"
          id="mailingListSuccess"
          defaultMessage="Cool, now you are in the loop"
        />
        <Notification
          type="error"
          id="mailingListError"
          defaultMessage="Oops, something didn't work as planned"
        />
        <Notification type="info" id="mailingListInfo" />
        <MetaWrapper>
          <Pre>
            <b>state:</b> {JSON.stringify(state.value, null, 2)}
          </Pre>

          <Pre>
            <b>context (ctx):</b> {JSON.stringify(context, null, 2)}
          </Pre>
        </MetaWrapper>
      </form>
    </FormWrapper>
  )
}
