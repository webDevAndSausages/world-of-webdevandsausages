import React, { useContext } from 'react'
import styled, { css } from '../../styles/styled-components'
import darken from 'polished/lib/color/darken'

import { NavLink } from 'react-router-dom'
import MobileNavbar from './MobileNavbar'
import { toRem, tablet } from '../../styles/helpers'
import NavLinks from './NavLinks'
import SocialLinks from './SocialLinks'
import Logo from './Logo'
import Sidebar from './Sidebar'
import SidebarMenu from './SidebarMenu'
import { UiContext } from '../../App'
import { Theme } from '../../models/UI'

const Wrapper = styled.nav<{ transparent: boolean; reverse: boolean }>`
  position: fixed;
  left: 0;
  box-sizing: border-box;
  z-index: 3;
  width: 100%;
  font-weight: 500;
  ${({ theme, transparent, reverse }) =>
    css`
      background: ${transparent
        ? 'transparent'
        : `${reverse ? theme.primaryOrange : theme.primaryBlue}`};
      box-shadow: ${!transparent ? '0 0 5px rgba(0, 0, 0, 0.5)' : 'none'};
    `};
  transition: background 300ms ease-out;
`

const NormalNavbar = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 25px;
  ${tablet(css`
    display: none;
  `)};
`

const StartWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 0 80px;
`

const EndWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: flex-end;
`

const NavTitleLink = styled(NavLink)`
  font-family: museo_sans500_Italic, sans-serif;
  font-size: 18px;
  ${({ theme }) =>
    css`
      line-height: ${toRem(theme.navHeight)};
    `};
  text-align: center;
  font-weight: 400;
  ${({ theme }) =>
    css`
      color: ${darken(0.1, theme.iconsColor)};
    `};
  text-decoration: none;
  transition: opacity 0.2s, transform 0.2s;
  cursor: pointer;
  &:hover,
  &:focus {
    opacity: 0.8;
  }
  &:active {
    transform: scale(0.95);
    opacity: 0.6;
  }
`

const Navbar = ({
  disableRegistration,
  isFeedbackLinkVisible,
  toggleNav
}: any) => {
  const { theme, showSidebar, isScrolled, showMobileNav } = useContext(
    UiContext
  )

  const reversed = theme === Theme.Reversed
  return (
    <div>
      <Wrapper transparent={isScrolled} reverse={reversed}>
        <NormalNavbar>
          <StartWrapper>
            <Logo />
            <NavTitleLink to="/">Web Dev &amp; Sausages</NavTitleLink>
            <NavLinks
              disableRegistration={disableRegistration}
              isFeedbackLinkVisible={isFeedbackLinkVisible}
            />
          </StartWrapper>

          <EndWrapper>
            <SocialLinks />
          </EndWrapper>
        </NormalNavbar>
        <MobileNavbar
          isScrolled={isScrolled}
          disableRegistration={disableRegistration}
          isFeedbackLinkVisible={isFeedbackLinkVisible}
          toggleNav={toggleNav}
          showMobileNav={showMobileNav}
          reverseTheme={reversed}
        />
      </Wrapper>
      {showSidebar && (
        <Sidebar>
          <SidebarMenu />
        </Sidebar>
      )}
    </div>
  )
}

export default Navbar
