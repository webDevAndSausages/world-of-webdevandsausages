import React from 'react'
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

export const Cursor = styled.input<{ blink: boolean }>`
  font-size: 24px;
  margin-left: 15px;
  padding-top: 5px;
  color: #cdee69;
  ${({ blink: b }) =>
    b
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
`

export const CursorInput = ({
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
    autoFocus
  />
)
