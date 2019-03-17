import React from 'react'
import styled, { css } from 'styled-components'
import { Waiting, TerminalInputProps } from './CurrentEventTerminal'
import { Out } from '../../components/terminal'
import { SpecialMode } from './Registration'

export const HelpOut = styled(Out)`
  padding: 10px 0 20px 20px;
`

const HelpOutTitle = styled(HelpOut)`
  font-weight: bold;
`

const Cmd = styled.span`
  color: ${({ theme }) => theme.primaryOrange};
  font-weight: bold;
`

const HelpOutForCmd = ({ cmd, help }) => (
  <HelpOut>
    <SpecialMode>{'<'}</SpecialMode>
    <Cmd>{cmd}</Cmd>
    <SpecialMode>{'>'}</SpecialMode>
    {' - '}
    {help}
  </HelpOut>
)

export const Help: React.FC<TerminalInputProps> = props => (
  <>
    <div style={{ padding: '20px 0' }}>
      <HelpOutTitle>Web dev & sausages cli usage</HelpOutTitle>
      <HelpOut>
        This is a tool for registering, cancelling (modifying), and checking the
        status and existance of your event registration. Both Cancelling and
        checking your registration require a verification token that you
        received upon registering.
      </HelpOut>
      <HelpOutForCmd
        cmd="r|register"
        help="Enters registration mode. the form requires at least a valid email.
        Please not that if you give a fake email, you will not receive email
        notifications about changes to your status. To exit without registering,
        press 'cancel'."
      />
      <HelpOutForCmd
        cmd="m|modify"
        help="To cancel your registration, enter a valid
               verification token (check your email)."
      />
      <HelpOutForCmd
        cmd="c|check"
        help="To check your registration, including your position on
               a waiting list, enter your valid verification token."
      />
      <HelpOutForCmd cmd="h|help" help="Return you to this help page." />
    </div>
    <Waiting {...props} />
  </>
)
