import React, { useEffect, useRef } from 'react'
// styles
import styled from '../../styles/styled-components'

import {
  machineConfig,
  MailingListContext,
  MailingListConfig
} from './mailingListMachineConfig'
import { map, delay } from 'rxjs/operators'
import { useApi } from '../../hooks/useApi'
import { useRxMachine, onEvent } from '../../hooks/useRxMachine'
import { isEmail } from '../../helpers/validation'
import { RequestFromApi, ApiRequest } from '../../models/ApiRequest'
// components
import Button from '../../components/Button'
import Notification from '../../components/Notification'
import { Input } from '../../components/forms/Input'
import { MetaWrapper, Pre } from '../../components/DevTools'

const FieldWrapper = styled.div`
  display: flex;
  justify-content: center;
`

interface Errors {
  email?: string
}

const TitleWrapper = styled.div`
  display: flex;
  justify-content: center;
  z-index: 10;
`

const Title = styled.h2`
  color: #fff;
  font-weight: 400;
  font-size: 20px;
  line-height: 150%;
  margin: 0;
  padding-bottom: 10px;
`

const FormWrapper = styled.div`
  padding: 20px;
`

export function MailingListForm() {
  const { request, query, reset: resetApi } = useApi(
    'mailingList',
    false,
    'post'
  )
  const emailInputRef = useRef()
  const { state, send, context } = useRxMachine<MailingListConfig, any>(
    machineConfig,
    {
      actions: {
        cacheEmail: (_ctx: MailingListContext, value: string) => ({
          email: value
        }),
        send: ({ email }: MailingListContext) =>
          query({ payload: { email, receivesMail: true } }),
        cacheRequest: (_ctx: MailingListContext, value: RequestFromApi) =>
          value,
        reset: (ctx: MailingListContext, value) => ({
          data: ApiRequest.NOT_ASKED(),
          email: ''
        })
      },
      guards: {
        isBadEmailFormat: ctx => !isEmail(ctx.email)
      }
    },
    [
      vals =>
        vals.pipe(
          onEvent('SUCCESS'),
          delay(5000),
          map(([_state, send]) => {
            resetApi()
            send({
              type: 'RESET'
            })
          })
        )
    ]
  )

  useEffect(() => {
    ApiRequest.match(request, {
      NOT_ASKED: () => null,
      OK: data => send({ type: 'SUCCESS', value: { data: request } }),
      NOT_OK: data => send({ type: 'FAILURE', value: { data: request } }),
      LOADING: () => send({ type: 'LOADING', value: { data: request } })
    })
  }, [request])

  return (
    <FormWrapper>
      <TitleWrapper>
        <Title>Join our mailing list to hear about upcoming events:</Title>
      </TitleWrapper>
      <form
        onSubmit={e => {
          e.preventDefault()
          send({ type: 'SUBMIT' })
        }}
      >
        <FieldWrapper>
          <Input
            id="email"
            type="email"
            placeholder="devaaja@gmail.com"
            onBlur={() => {
              send({ type: 'EMAIL_BLUR' })
            }}
            onKeyPress={e => {
              if (e.key === 'Enter') {
                e.preventDefault()
                send({ type: 'SUBMIT' })
              }
            }}
            value={context.email}
            err={state.matches('emailErr')}
            active={context.email.length > 0}
            disabled={state.matches('SUBMIT')}
            onChange={e => {
              send({
                type: 'ENTER_EMAIL',
                value: e.target.value
              })
            }}
            ref={emailInputRef}
          />
          <Button
            type="submit"
            loading={ApiRequest.is.LOADING(context.data)}
            secondary
            disabled={!isEmail(context.email)}
            valid={isEmail(context.email)}
            white
          >
            {'+'}
          </Button>
        </FieldWrapper>
        <Notification
          type="success"
          id="mailingListSuccess"
          defaultMessage="Cool, now you are in the loop"
          show={ApiRequest.is.OK(context.data)}
        />
        <Notification
          type="error"
          id="mailingListError"
          defaultMessage="Oops, something didn't work as planned"
          show={ApiRequest.is.NOT_OK(context.data)}
        />
      </form>
    </FormWrapper>
  )
}
