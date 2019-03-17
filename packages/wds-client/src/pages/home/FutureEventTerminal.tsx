import React from 'react'
import styled, { css } from '../../styles/styled-components'
import lighten from 'polished/lib/color/lighten'
import transparentize from 'polished/lib/color/transparentize'

import { Terminal, Out, Prompt, TerminalOut } from '../../components/terminal'
import Mailto from '../../components/Mailto'

const ConsoleLink = styled.span`
  color: ${lighten(0.1, '#cdee69')};
  border-bottom: 1px #cdee69 dashed;
  ${({ theme }) =>
    css`
      background-color: ${transparentize(0.8, theme.primaryOrange)};
    `};
`

const FutureEvent = () => (
  <Terminal>
    <TerminalOut title="what" detail="The next volume is in the works..." />
    <TerminalOut
      title="when"
      detail="Join our mailing list &uarr; or follow us on Twitter to find out"
    />
    <Prompt>[?] help by sponsoring, speaking, or organizing</Prompt>
    <Out>
      Awesome! Contact{' '}
      <Mailto email="leo.melin@gofore.com">
        <ConsoleLink>Leo</ConsoleLink>
      </Mailto>{' '}
      or{' '}
      <Mailto email="richard.vancamp@gofore.com">
        <ConsoleLink>Richard</ConsoleLink>
      </Mailto>
    </Out>
  </Terminal>
)

export default FutureEvent
