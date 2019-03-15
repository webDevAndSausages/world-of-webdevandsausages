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
  | 'error'
  | 'back'
  | 'forward'

const commands: { [key: string]: Action } = {
  r: 'register',
  m: 'modify',
  c: 'check',
  h: 'help',
  e: 'error',
  b: 'back',
  f: 'forward'
}

const commandsRegex = /(r|register|m|modify|c|check|c|help)/

export const CursorInput = ({
  onCommand,
  active
}: {
  onCommand: (a: Action) => void
  active: boolean
}) => {
  const [value, setValue] = useState('')
  const onKeyDown = (e: any) => {
    if (!active) return

    const RETURN = e.keyCode === 13
    const BACK = e.keyCode === 38
    const FORWARD = e.keyCode === 40

    if (RETURN) {
      if (value && commandsRegex.test(value)) {
        return onCommand(commands[value[0]])
      }
      return onCommand('error')
    }
    // back event will propagate up to the page if no value entered and active
    if (value && BACK) {
      return onCommand('back')
    }
    if (FORWARD) {
      return onCommand('forward')
    }
  }

  return (
    <Cursor
      placeholder="_"
      value={value}
      onChange={(e: any) => setValue(e.target.value.toLowerCase())}
      blink={!value}
      onKeyDown={onKeyDown}
      autoFocus
      disabled={!active}
    />
  )
}
