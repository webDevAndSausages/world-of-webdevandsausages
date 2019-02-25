import React from 'react'
import styled, { css } from '../../styles/styled-components'
import lighten from 'polished/lib/color/lighten'
import transparentize from 'polished/lib/color/transparentize'

import { Console, Screen, EventDetailLabel, EventDetail } from './CurrentEvent'
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
  <Console>
    <Screen>
      <EventDetailLabel>$ what</EventDetailLabel>
      <EventDetail>The next volume is in the works...</EventDetail>
      <EventDetailLabel>$ when</EventDetailLabel>
      <EventDetail>
        Join our mailing list &uarr; or follow us on Twitter to find out
      </EventDetail>
      <EventDetailLabel>
        [?] help by sponsoring, speaking, or organizing
      </EventDetailLabel>
      <EventDetail>
        Awesome! Contact{' '}
        <Mailto email="leo.melin@gofore.com">
          <ConsoleLink>Leo</ConsoleLink>
        </Mailto>{' '}
        or{' '}
        <Mailto email="richard.vancamp@gofore.com">
          <ConsoleLink>Richard</ConsoleLink>
        </Mailto>
      </EventDetail>
    </Screen>
  </Console>
)

export default FutureEvent
