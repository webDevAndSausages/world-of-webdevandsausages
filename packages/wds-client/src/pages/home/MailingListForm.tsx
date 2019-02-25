import React, { useEffect, useRef } from 'react'
// styles
import styled, { css } from '../../styles/styled-components'
import { toRem, phone, tablet } from '../../styles/helpers'
import { theme } from '../../styles/theme'
import transparentize from 'polished/lib/color/transparentize'

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
import { MetaWrapper, Pre } from '../../components/DevTools'

const inputBackground = transparentize(0.8, theme.secondaryBlue)

const Input = styled.input<any>`
  border: 2px solid ${theme.secondaryBlue};
  background: ${inputBackground};
  border-radius: 3px;
  color: ${theme.secondaryBlue};
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
  &:readonly {
    outline: none;
    background: #0b7ebc;
  }
  &:focus,
  &:active {
    outline: none;
    background: ${theme.secondaryBlue};
  }
  /* removes chrome's yellow background */
  &,
  &:-webkit-autofill {
    -webkit-text-fill-color: #fff;
    box-shadow: 0 0 0px 1000px
      ${({ active }) => (active ? theme.secondaryBlue : inputBackground)} inset;
    -webkit-box-shadow: 0 0 0px 1000px
      ${({ active }) => (active ? theme.secondaryBlue : inputBackground)} inset;
  }
  &:-webkit-autofill:hover,
  &:-webkit-autofill:focus,
  &:-webkit-autofill:active {
    -webkit-text-fill-color: #fff;
    box-shadow: 0 0 0px 1000px ${theme.secondaryBlue} inset;
    -webkit-box-shadow: 0 0 0px 1000px ${theme.secondaryBlue} inset;
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
  ${({ err, theme }) =>
    err &&
    css`
      border-color: ${({ theme }) => theme.notificationError};
    `}
`

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
          onEvent('SUCCESS', 'FAILURE'),
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
            loading={state.matches('SUBMIT')}
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
