import React, { useState } from 'react'
import styled, { css, keyframes } from 'styled-components'

export const blink = keyframes`
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

export const Cursor = styled.input<{
  blink: boolean
  disabled: boolean
}>`
  font-size: 24px;
  padding-top: 5px;
  margin-left: 1.2rem;
  line-height: 100%;
  font-size: 1.2rem;
  color: ${({ disabled }) => (disabled ? '#fff' : '#e0e0e0')};
  ${({ blink: b, disabled: d }) =>
    b && d
      ? css`
          animation: 1s ${blink} 1s infinite;
        `
      : undefined};
  background: transparent;
  border: none;
  box-shadow: none;
  width: 100%;
  padding-left: 5px;
  outline: none;
  ::placeholder {
    color: #fff;
  }
  &:disabled {
    color: #e0e0e0;
  }
`

export type Action =
  | 'wait'
  | 'register'
  | 'modify'
  | 'check'
  | 'help'
  | 'invalid'
  | 'back'
  | 'forward'

const commands: { [key: string]: Action } = {
  r: 'register',
  m: 'modify',
  c: 'check',
  h: 'help',
  i: 'invalid',
  b: 'back',
  f: 'forward'
}

const commandsRegex = /(r|register|m|modify|c|check|h|help)/

export type OnCmd = (a: { type: Action; cmd?: string }) => void

export const CursorInput = ({
  onCommand,
  active,
  onKeyDown
}: {
  onCommand: OnCmd
  active: boolean
  onKeyDown?: (e: React.KeyboardEvent<HTMLDivElement>) => void
}) => {
  const [value, setValue] = useState('')

  const onKeyDownDefault = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (!active) return

    const RETURN = e.keyCode === 13
    const BACK = e.keyCode === 38
    const FORWARD = e.keyCode === 40

    if (RETURN) {
      if (value && commandsRegex.test(value)) {
        return onCommand({ type: commands[value[0]] })
      }
      return onCommand({ type: 'invalid', cmd: value })
    }
    // back event will propagate up to the page if no value entered and active
    if (value && BACK) {
      return onCommand({ type: 'back' })
    }
    if (FORWARD) {
      return onCommand({ type: 'forward' })
    }
  }

  return (
    <React.Fragment>
      <label id="terminal-command-input-label" htmlFor="terminal-command-input" />
      <Cursor
        id="terminal-command-input"
        placeholder="_"
        value={value}
        onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
          setValue(e.target.value.toLowerCase())
        }
        blink={!value}
        onKeyDown={onKeyDown ? onKeyDown : onKeyDownDefault}
        autoFocus
        disabled={!active}
      />
    </React.Fragment>
  )
}
