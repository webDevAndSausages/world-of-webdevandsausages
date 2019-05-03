import React from 'react'

import { Terminal, Out, Prompt, TerminalOut, TerminalLink } from '../../components/terminal'
import Mailto from '../../components/Mailto'

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
        <TerminalLink>Leo</TerminalLink>
      </Mailto>{' '}
      or{' '}
      <Mailto email="richard.vancamp@gofore.com">
        <TerminalLink>Richard</TerminalLink>
      </Mailto>
    </Out>
  </Terminal>
)

export default FutureEvent
