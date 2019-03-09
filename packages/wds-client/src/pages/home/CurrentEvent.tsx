import React, { useReducer, useState } from "react"
import styled, { css, keyframes } from "styled-components"
import darken from "polished/lib/color/darken"

import format from "date-fns/format"
import { over, lensProp } from "ramda"
import { toRem, phone, tablet } from "../../styles/helpers"
import { theme } from "../../styles/theme"
import Markdown from "react-markdown/with-html"
import { EventData, Event as EventType } from "../../models/Event"
import { unionize, ofType, UnionOf } from "unionize"

import Spinner from "../../components/Spinner"
import FutureEvent from "./FutureEvent"

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
  font-size: 24px;
  color: #fff;
  margin: 0;
  padding: 15px 0;
  line-height: 120%;
`

export const EventDetail = styled.div`
  margin: 0;
  padding-left: 1.5rem;
  line-height: 100%;
  font-size: 24px;
  ${({ theme }) =>
    css`
      color: #cdee69;
    `};
`

export const Screen = styled.div`
  display: flex;
  flex-direction: column;
  ${({ theme }) =>
    css`
      background: ${darken(0.1, theme.primaryBlue)};
    `};
  box-sizing: border-box;
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  border-bottom-left-radius: 5px;
  border-bottom-right-radius: 5px;
  opacity: 0.8;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.19), 0 6px 6px rgba(0, 0, 0, 0.23);
`

const Menu = styled.div`
  max-width: 1000px;
  display: flex;
  opacity: 0.7;
  height: 25px;
  background-color: #bbb;
  margin: 0 auto;
  border-top-right-radius: 5px;
  border-top-left-radius: 5px;
  box-shadow: 0 19px 38px rgba(0, 0, 0, 0.3), 0 15px 12px rgba(0, 0, 0, 0.22);
`

const FakeButton = styled.div`
  height: 10px;
  width: 10px;
  border-radius: 50%;
  border: 1px solid #000;
  position: relative;
  top: 6px;
  left: 6px;
  background-color: #ff3b47;
  border-color: #9d252b;
  display: inline-block;
`

const FakeMinimize = styled(FakeButton)`
  left: 11px;
  background-color: #ffc100;
  border-color: #9d802c;
`

const FakeZoom = styled(FakeButton)`
  left: 16px;
  background-color: #00d742;
  border-color: #049931;
`

const blink = keyframes`
  0% {
    opacity: 0;
  };
  40% {
    opacity: 0;
  };
  50% {
    opacity: 1;
  };
  90% {
    opacity: 1;
  };
  100% {
    opacity: 0;
  };
`

const Cursor = styled.input<{ blink: boolean }>`
  font-size: 24px;
  color: black;
  ${({ blink: b }) =>
    b
      ? css`
          animation: 1s ${blink} 1s infinite;
        `
      : undefined};
  background: transparent;
  border: none;
  box-shadow: none;
  outline: none;
  ::placeholder {
    color: #fff;
  }
`

const CursorInput = ({
  commandValue,
  onChange,
  onKeyPress
}: {
  commandValue: string
  onChange: (e: any) => any
  onKeyPress: any
}) => (
  <Cursor
    placeholder="_"
    value={commandValue}
    onChange={onChange}
    blink={!commandValue}
    onKeyPress={onKeyPress}
  />
)

const SponsorLogo = styled.img`
  width: ${toRem(400)};
  padding-bottom: 20px;
  opacity: 0.9;
  transition: opacity 125ms ease-in-out;
  &:hover {
    opacity: 1;
  }
`

export const Console = ({ children }) => (
  <EventWrapper>
    <Menu>
      <FakeButton />
      <FakeMinimize />
      <FakeZoom />
    </Menu>
    {children}
  </EventWrapper>
)

interface ConState {
  prompt: string
  last: any
}

const ConsoleState = unionize({
  Waiting: ofType<ConState>(),
  Registering: ofType<ConState>(),
  Modifing: ofType<ConState>(),
  Checking: ofType<ConState>(),
  Helping: ofType<ConState>()
})

type ConsoleStateType = UnionOf<typeof ConsoleState>

type Action = "wait" | "register" | "modify" | "check" | "help" | "back"

const defaultPrompt = "Commands: register [r], modify [m], check [c], help [h], back [b/esc]:"

const defaultState = ConsoleState.Waiting({
  prompt: defaultPrompt,
  last: defaultPrompt
})

const updates = {
  wait: () => defaultState,
  register: (state: ConsoleStateType) =>
    ConsoleState.Registering({ prompt: "Email:", last: state }),
  modify: (state: ConsoleStateType) =>
    ConsoleState.Modifing({ prompt: "Registration token (received by email):", last: state }),
  check: (state: ConsoleStateType) =>
    ConsoleState.Modifing({ prompt: "Registration token (received by email):", last: state }),
  help: (state: ConsoleStateType) =>
    ConsoleState.Helping({ prompt: "To return press any key", last: state }),
  back: (state: ConsoleStateType) => state.last
}

const consoleReducer = (state: ConsoleStateType, action: Action) =>
  updates[action] ? updates[action](state) : defaultState

const commands: { [key: string]: Action } = {
  r: "register",
  m: "modify",
  c: "check",
  h: "help",
  b: "back"
}

const RegistrationConsole = ({ event, children }: { event: EventData; children?: any }) => {
  const [consoleState, dispatch] = useReducer(consoleReducer, defaultState)
  const [commandValue, setCommand] = useState("")
  const handleCommand = (e: any) => {
    if (commands[e.target.value.toLowerCase()]) {
      setCommand(e.target.value)
    }
  }
  const dispatchCommand = (e: any) => {
    if (e.charCode == "13" && commandValue) {
      dispatch(commands[commandValue.toLowerCase()])
      setCommand("")
    }
    if (e.charCode === "27") {
      dispatch("back")
      setCommand("")
    }
  }

  const getPrompt = ConsoleState.match({ default: ({ prompt }) => prompt })

  return (
    <div id="current-event-console">
      <SponsorAnnouncement>Sponsored by</SponsorAnnouncement>
      {event.sponsor && (
        <a href={event.sponsorLink || null}>
          <SponsorLogo src={`/sponsor-logos/${event.sponsor.toLowerCase()}-logo.svg`} />
        </a>
      )}
      <Console>
        <Screen>
          <EventDetailLabel>$ which</EventDetailLabel>
          <EventDetail>Volume {event.volume}</EventDetail>
          <EventDetailLabel>$ when</EventDetailLabel>
          <EventDetail>{event.date}</EventDetail>
          <EventDetailLabel>$ what</EventDetailLabel>
          <EventDetail>
            <Markdown source={event.details} escapeHtml={false} />
          </EventDetail>
          <EventDetailLabel>$ where</EventDetailLabel>
          <EventDetail>
            <Markdown source={event.location} escapeHtml={false} />
          </EventDetail>
          <EventDetailLabel>$ who</EventDetailLabel>
          <EventDetail>
            <Markdown source={event.contact} escapeHtml={false} />
          </EventDetail>
          <EventDetailLabel>
            $ {getPrompt(consoleState as ConsoleStateType)}
            <CursorInput
              commandValue={commandValue}
              onChange={handleCommand}
              onKeyPress={dispatchCommand}
            />
          </EventDetailLabel>
        </Screen>
      </Console>
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
            const formattedData = over(lensProp("date"), date =>
              format(date, "MMMM Do, YYYY, HH:mm")
            )(data)
            return <RegistrationConsole event={formattedData} />
          }
        })}
    </>
  )
}

export default Event

/*
  <RegistrationConsole event={event}>
      <EventDetailLabel>[?] coming</EventDetailLabel>
      <EventDetailLabel onKeyPress={this.handleKeyPress}>
        $ <Cursor placeholder="_" />
      </EventDetailLabel>
    </RegistrationConsole>

  <EventConsumer
        renderOpenEvent={this.renderEvent}
        renderOpenEventWithRegistration={this.renderEventWithRegistration}
        map={event => ({
          eventDate: event.datetime
            ? format(event.datetime, 'MMMM Do, YYYY, HH:mm')
            : '',
          ...event
        })}
      />
      */
