import React, { Component } from 'react'
import styled, { css } from '../../styles/styled-components'
import darken from 'polished/lib/color/darken'
import R from 'ramda'
import { toRem, tablet, phone } from '../../styles/helpers'
import NavLinks, { NavSeparator } from './NavLinks'
import SocialLinks from './SocialLinks'
import Logo from './Logo'
import Svg from '../Svg'

import { theme } from '../../styles/theme'

const MenuIcon = () => (
  <Svg
    xmlns="http://www.w3.org/2000/svg"
    width="24"
    height="24"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
    className="feather feather-menu"
  >
    <title>menu</title>
    <line x1="3" y1="12" x2="21" y2="12" />
    <line x1="3" y1="6" x2="21" y2="6" />
    <line x1="3" y1="18" x2="21" y2="18" />
  </Svg>
)

const Wrapper = styled.div<any>`
  display: none;
  ${({ theme }) =>
    tablet(css`
      display: flex;
      align-items: center;
      justify-content: space-between;
      height: ${toRem(theme.navHeight)};
    `)};
`

const SecondaryMenu = styled.div<any>`
  position: absolute;
  ${({ theme }) =>
    css`
      top: ${toRem(theme.navHeight)};
    `};
  left: 0;
  right: 0;
  ${tablet((p: any) =>
    p.open
      ? css`
          height: ${toRem(theme.navHeight)};
        `
      : css`
          height: 0;
        `
  )};
  ${phone((p: any) =>
    p.open
      ? css`
          height: ${toRem(theme.navHeight * 1.8)};
          flex-direction: column;
        `
      : css`
          height: 0;
        `
  )};
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  justify-content: space-between;
  padding: 0 ${toRem(20)};
  transition: height 0.1s;
  user-select: none;
  -webkit-overflow-scrolling: touch;
  overflow-x: scroll;
  overflow-y: hidden;
  ${({ isScrolled, reverse }) =>
    isScrolled
      ? css`
          background: ${reverse ? '#f7b733' : '#52bdf6'};
        `
      : css`
          background: ${reverse ? '#52bdf6' : '#f7b733'};
        `};
  color: #868686;
  ${p =>
    p.open &&
    css`
      border-top: 1px solid ${darken(0.2, '#d3d3d3')};
      border-bottom: 1px solid ${darken(0.2, '#d3d3d3')};
    `};
`

const MenuIconWrapper = styled.div<any>`
  transition: transform 0.1s;
  ${p =>
    p.rotate &&
    css`
      transform-origin: 50% 55%;
      transform: rotate(90deg);
    `};
`

export const resetInput = css`
  background: none;
  outline: none;
  border: none;
`

export const NavButton = styled.button<any>`
  ${resetInput};
  flex: 0 0 auto;
  min-width: ${theme.navHeight};
  height: ${theme.navHeight};
  text-align: center;
  vertical-align: middle;
  cursor: pointer;
  padding: ${toRem(18)} ${toRem(26)};
  color: ${({ theme }) => theme.subduedTexTColor};
  ${(p: any) =>
    p.active &&
    css`
      background: rgba(0, 0, 0, 0.07);
    `};
`

interface Props {
  showMobileNav?: boolean
  isScrolled?: boolean
  reverseTheme?: boolean
  disableRegistration?: boolean
  isFeedbackLinkVisible?: boolean
  toggleNav: boolean
}

const MobileNavbar = ({
  showMobileNav,
  isScrolled,
  reverseTheme,
  toggleNav
}: Props) => (
  <Wrapper>
    <Logo />

    <NavButton onClick={toggleNav} active={showMobileNav}>
      <MenuIconWrapper rotate={showMobileNav ? 'rotate' : undefined}>
        <MenuIcon />
      </MenuIconWrapper>
    </NavButton>

    <SecondaryMenu
      open={showMobileNav}
      isScrolled={isScrolled}
      reverse={reverseTheme}
    >
      <NavLinks />
      <SocialLinks />
    </SecondaryMenu>
  </Wrapper>
)

export default MobileNavbar
