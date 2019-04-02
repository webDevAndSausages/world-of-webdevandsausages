import React from 'react'
import styled, { css } from 'styled-components'

const Menu = styled.div<{ isExpanded: boolean }>`
  display: flex;
  opacity: 0.7;
  height: 25px;
  background-color: ${({ theme }) => theme.terminalGray};
  margin: 0 auto;
  border-top-right-radius: 5px;
  border-top-left-radius: 5px;
  max-width: 1000px;
  ${({ isExpanded }) =>
    isExpanded &&
    css`
      max-width: 95%;
    `}
  box-shadow: 0 19px 38px rgba(0, 0, 0, 0.3), 0 15px 12px rgba(0, 0, 0, 0.22);
`

const TerminalButton = styled.div`
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

const FakeMinimize = styled(TerminalButton)`
  left: 11px;
  background-color: #ffc100;
  border-color: #9d802c;
`

const Zoom = styled(TerminalButton)`
  left: 16px;
  background-color: #00d742;
  border-color: #049931;
  z-index: 100;
  &:hover {
    cursor: pointer;
    :before {
      content: 'x';
      color: hsl(0, 0%, 40%);
      position: absolute;
      font-weight: 300;
      font-family: Arial, sans-serif;
      font-size: 10px;
      left: 2px;
      top: -1px;
      opacity: 0.7;
    }
  }
`

interface TabBarProps {
  isExpanded: boolean
  toggleTerminalSize: () => void
}

export const TabBar: React.FC<TabBarProps> = ({
  isExpanded,
  toggleTerminalSize
}) => (
  <Menu isExpanded={isExpanded}>
    <TerminalButton />
    <FakeMinimize />
    <Zoom onClick={toggleTerminalSize} />
  </Menu>
)
