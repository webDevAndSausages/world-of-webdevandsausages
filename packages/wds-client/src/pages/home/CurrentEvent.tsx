import React, { useReducer, useState } from 'react'
import styled, { css, keyframes } from 'styled-components'
import darken from 'polished/lib/color/darken'

import format from 'date-fns/format'
import { over, lensProp } from 'ramda'
import { toRem, phone, tablet } from '../../styles/helpers'
import { theme } from '../../styles/theme'
import Markdown from 'react-markdown/with-html'
import { EventData, Event as EventType } from '../../models/Event'
import { ConsoleRegistration } from './TerminalRegistration'

import Spinner from '../../components/Spinner'
import FutureEvent from './FutureEvent'

import { ConsoleState, ConsoleStateType } from '../../models/ConsoleState'
import { Terminal, CursorInput } from '../../components/terminal'

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

export const EventDetailLabel = styled.label`
  font-size: 1.2rem;
  font-weight: bold;
  color: ${({ theme }) => theme.primaryOrange};
  margin: 0;
  padding: 15px 0;
  line-height: 120%;
`

export const EventDetail = styled.div`
  margin: 0;
  padding-left: 1.2rem;
  line-height: 100%;
  font-size: 1.2rem;
  ${({ theme }) =>
    css`
      color: #fff;
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

type Action = 'wait' | 'register' | 'modify' | 'check' | 'help' | 'back'

const defaultPrompt =
  'Commands: register [r], modify [m], check [c], help [h], back [</b/esc]:'

const defaultState = ConsoleState.Waiting({
  prompt: defaultPrompt,
  last: defaultPrompt
})

const updates = {
  wait: () => defaultState,
  register: (state: ConsoleStateType) =>
    ConsoleState.Registering({ last: state }),
  modify: (state: ConsoleStateType) => ConsoleState.Modifing({ last: state }),
  check: (state: ConsoleStateType) => ConsoleState.Checking({ last: state }),
  help: (state: ConsoleStateType) =>
    ConsoleState.Helping({ prompt: 'Back [b/esc]:', last: state }),
  back: (state: ConsoleStateType) => state.last
}

const consoleReducer = (state: ConsoleStateType, action: Action) =>
  updates[action] ? updates[action](state) : defaultState

const commands: { [key: string]: Action } = {
  r: 'register',
  m: 'modify',
  c: 'check',
  h: 'help',
  b: 'back'
}

const Dollar = () => <span className="dollar">$</span>

const RegistrationConsole = ({
  event
}: {
  event: EventData
  children?: any
}) => {
  const [consoleState, dispatch] = useReducer(consoleReducer, defaultState)
  const [commandValue, setCommand] = useState('')
  const handleCommand = (e: any) => {
    if (commands[e.target.value.toLowerCase()]) {
      setCommand(e.target.value)
    }
  }
  const dispatchCommand = (e: any) => {
    if (e.charCode == '13' && commandValue) {
      dispatch(commands[commandValue.toLowerCase()])
      setCommand('')
    }
    if (e.charCode === '27' || e.charCode === '37') {
      dispatch('back')
      setCommand('')
    }
  }

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
        <EventDetailLabel>
          <Dollar /> which
        </EventDetailLabel>
        <EventDetail>Volume {event.volume}</EventDetail>
        <EventDetailLabel>
          <Dollar /> when
        </EventDetailLabel>
        <EventDetail>{event.date}</EventDetail>
        <EventDetailLabel>
          <Dollar /> what
        </EventDetailLabel>
        <EventDetail>
          <Markdown source={event.details} escapeHtml={false} />
        </EventDetail>
        <EventDetailLabel>
          <Dollar /> where
        </EventDetailLabel>
        <EventDetail>
          <Markdown source={event.location} escapeHtml={false} />
        </EventDetail>
        <EventDetailLabel>
          <Dollar /> who
        </EventDetailLabel>
        <EventDetail>
          <Markdown source={event.contact} escapeHtml={false} />
        </EventDetail>
        {ConsoleState.match(consoleState, {
          Registering: () => <ConsoleRegistration eventId={event.id} />,
          default: ({ prompt }) => (
            <>
              <EventDetailLabel>$ {prompt}</EventDetailLabel>
              <CursorInput
                commandValue={commandValue}
                onChange={handleCommand}
                onKeyPress={dispatchCommand}
              />
            </>
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
