import React, { useContext } from 'react'
import styled, { css } from 'styled-components'
import darken from 'polished/lib/color/darken'
import { toRem, phone, tablet } from '../../styles/helpers'
import { theme } from '../../styles/theme'
export * from './Input'
export * from './Output'
import { TabBar } from './TabBar'
import { UiContext } from '../../App'

export const TerminalWrapper = styled.article<{ isExpanded: boolean }>`
  font-family: Inconsolata;
  font-size: 1.2rem;
  text-align: left;
  margin: auto;
  padding-top: 1rem;
  width: 60%;
  ${({ isExpanded }) =>
    isExpanded &&
    css`
      width: 100%;
      z-index: 100;
      top: 0;
      transition: width 600ms ease-out;
    `}
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

export const Screen = styled.div<{ isExpanded: boolean }>`
  display: flex;
  flex-direction: column;
  ${({ theme }) =>
    css`
      background: ${darken(0.1, theme.secondaryBlue)};
    `};
  box-sizing: border-box;
  max-width: 1000px;
  ${({ isExpanded }) =>
    isExpanded &&
    css`
      max-width: 95%;
      height: 100%;
    `}
  margin: 0 auto;
  padding: 20px;
  border-bottom-left-radius: 5px;
  border-bottom-right-radius: 5px;
  min-height: 300px;
  max-height: 100%;
  overflow: hidden;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.19), 0 6px 6px rgba(0, 0, 0, 0.23);
`

export const Terminal = ({ children }) => {
  const { isTerminalExpanded, toggleTerminalSize, ...rest } = useContext(
    UiContext
  )
  console.log({ isTerminalExpanded, toggleTerminalSize, ...rest })
  return (
    <TerminalWrapper isExpanded={isTerminalExpanded}>
      <TabBar
        isExpanded={isTerminalExpanded}
        toggleTerminalSize={toggleTerminalSize}
      />
      <Screen isExpanded={isTerminalExpanded}>{children}</Screen>
    </TerminalWrapper>
  )
}
