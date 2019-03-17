import React from 'react'
import styled from 'styled-components'
import { HelpOut } from './Help'
import { Waiting, TerminalInputProps } from './CurrentEvent'

const Warning = styled.span`
  color: ${({ theme }) => theme.notificationError};
`

interface InvalidCmdProps extends TerminalInputProps {
  cmd: string
}

export const InvalidCmd: React.FC<InvalidCmdProps> = ({ cmd, ...rest }) => (
  <>
    <HelpOut>
      <Warning>Command not found:</Warning> {cmd}
    </HelpOut>
    <Waiting {...rest} />
  </>
)
