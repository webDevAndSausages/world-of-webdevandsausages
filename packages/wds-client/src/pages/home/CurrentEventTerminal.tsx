import React, { useReducer, useContext } from 'react'
import styled, { css } from '../../styles/styled-components'
import darken from 'polished/lib/color/darken'
import produce from 'immer'

import format from 'date-fns/format'
import { over, lensProp } from 'ramda'
import { toRem, phone, tablet } from '../../styles/helpers'
import { theme } from '../../styles/theme'
import { EventData, Event as EventType } from '../../models/Event'
import { EventRegistration } from './Registration'
import { CheckRegistration } from './CheckRegistration'
import { CancelRegistration } from './CancelRegistration'
import { InvalidCmd } from './InvalidCommand'
import { Help } from './Help'
import { Prompt } from '../../components/terminal'

import Spinner from '../../components/Spinner'
import FutureEvent from './FutureEventTerminal'

import {
  Terminal,
  CursorInput,
  TerminalOut,
  Action
} from '../../components/terminal'

const InnerWrapper = styled.div<any>`
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  margin: auto;
  ${p =>
    p.marginTop &&
    css`
      margin-top: ${toRem(p.marginTop)};
    `};
  ${p =>
    p.marginBottom &&
    css`
      margin-top: ${toRem(p.marginBottom)};
    `};
`
export const EventWrapper = styled.article`
  font-size: ${toRem(20)};
  text-align: left;
  margin: auto;
  padding-top: 1rem;
  width: 60%;
  @media (max-width: ${1600 / 18}em) {
    width: 70%;
  }
  ${tablet(css`
    width: 80%;
  `)};
  ${phone(css`
    width: 90%;
  `)};
  color: ${darken(0.2, theme.iconsColor)};
`

const SponsorAnnouncement = styled.h3`
  font-size: 1.5em;
  font-family: museo_sans500_Italic, sans-serif;
  ${({ theme }) =>
    css`
      color: ${theme.primaryOrange};
    `};
`

const SponsorLogo = styled.img`
  width: ${toRem(400)};
  padding-bottom: 20px;
  opacity: 0.9;
  transition: opacity 125ms ease-in-out;
  &:hover {
    opacity: 1;
  }
`

const defaultPrompt =
  'Registration modes: register [r], modify [m], check [c], help [h]'

export interface TerminalInputProps {
  onCommand: (a: { type: Action; cmd?: string }) => void
  active: boolean
}

export const Waiting: React.FC<TerminalInputProps> = ({
  onCommand,
  active
}) => (
  <>
    <Prompt>$ {defaultPrompt}</Prompt>
    <CursorInput onCommand={onCommand} active={active} />
  </>
)

interface TerminalState {
  current: any
  history: any[]
  cmd: string[]
}

const defaultState = {
  current: 0,
  history: [Waiting],
  cmd: []
}

const updates = {
  wait: (state: TerminalState) => {
    state.history.push(Waiting)
    state.current++
  },
  register: (state: TerminalState) => {
    state.history.push(EventRegistration)
    state.current++
  },
  modify: (state: TerminalState) => {
    state.history.push(CancelRegistration)
    state.current++
  },
  check: (state: TerminalState) => {
    state.history.push(CheckRegistration)
    state.current++
  },
  help: (state: TerminalState) => {
    console.log('called')
    state.history.push(Help)
    state.current++
  },
  back: (state: TerminalState) => {
    const current = state.history[state.current - 1]
    if (current) {
      state.current--
    }
  },
  forward: (state: TerminalState) => {
    const next = state.history[state.current + 1]
    if (next) {
      state.current++
    }
  },
  invalid: (state: TerminalState, cmd: string) => {
    state.history.push(InvalidCmd)
    state.cmd[state.current] = cmd
    state.current++
  }
}

const consoleReducer = (
  state: TerminalState,
  action: { type: Action; cmd: string }
) =>
  updates[action.type]
    ? (produce as any)(updates[action.type])(state, action.cmd)
    : defaultState

const RegistrationConsole = ({
  event
}: {
  event: EventData
  children?: any
}) => {
  const [consoleState, dispatch] = useReducer(consoleReducer, defaultState)

  return (
    <div id="current-event-console">
      <SponsorAnnouncement>Sponsored by</SponsorAnnouncement>
      {event.sponsor && (
        <a href={event.sponsorLink || null}>
          <SponsorLogo
            src={`/sponsor-logos/${event.sponsor.toLowerCase()}-logo.svg`}
          />
        </a>
      )}
      <Terminal>
        <TerminalOut title="which" detail={`Volume ${event.volume}`} />
        <TerminalOut title="when" detail={event.date} />
        <TerminalOut title="what" detail={event.details} />
        <TerminalOut title="where" detail={event.location} />
        <TerminalOut title="who" detail={event.contact} />
        {event.status !== 'VISIBLE' && consoleState.history.map((Component: any, i: number) => {
          return (
            <Component
              key={i}
              onCommand={dispatch}
              eventId={event.id}
              active={i === consoleState.current}
              cmd={consoleState.cmd[i - 1]}
            />
          )
        })}
      </Terminal>
    </div>
  )
}

const Loading = () => (
  <InnerWrapper>
    <Spinner />
  </InnerWrapper>
)

function Event(event: any) {
  return (
    <>
      {event &&
        EventType.match(event, {
          LOADING: () => <Loading />,
          NONE: () => <FutureEvent />,
          ERROR: () => <FutureEvent />,
          default: ({ data }: any) => {
            const formattedData = over(lensProp('date'), date =>
              format(date, 'MMMM Do, YYYY, HH:mm')
            )(data)
            return <RegistrationConsole event={formattedData} />
          }
        })}
    </>
  )
}

export default Event
