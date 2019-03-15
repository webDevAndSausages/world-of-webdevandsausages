import React from 'react'
import styled, { css } from 'styled-components'
import darken from 'polished/lib/color/darken'
import { toRem, phone, tablet } from '../../styles/helpers'
import { theme } from '../../styles/theme'
export * from './Input'
export * from './TerminalDetail'
import { TabBar } from './TabBar'

export const TerminalWrapper = styled.article`
  font-family: Inconsolata;
  font-size: 1.2rem;
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

export const Screen = styled.div`
  display: flex;
  flex-direction: column;
  ${({ theme }) =>
    css`
      background: #4e4e4e};
    `};
  box-sizing: border-box;
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  border-bottom-left-radius: 5px;
  border-bottom-right-radius: 5px;
  min-height: 300px;
  max-height: 100%;
  overflow: auto;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.19), 0 6px 6px rgba(0, 0, 0, 0.23);
`

export const Terminal = ({ children }) => {
  return (
    <TerminalWrapper>
      <TabBar />
      <Screen>{children}</Screen>
    </TerminalWrapper>
  )
}
