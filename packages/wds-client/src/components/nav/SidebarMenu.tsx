import React from 'react'
import styled, { css } from '../../styles/styled-components'
import { NavLink } from 'react-router-dom'
import darken from 'polished/lib/color/darken'

// import { sections } from '../../routes/admin/AdminPanel'
import { toRem } from '../../styles/helpers'
import { compose, toLower, join, split } from 'ramda'

const StyledLink = styled<any>(NavLink)`
  display: inline-block;
  text-decoration: none;
  color: inherit;
  cursor: pointer;
  padding: ${toRem(2)} ${toRem(8)};
  margin: ${toRem(-2)} ${toRem(-8)};
  ${({ theme }) => css`
    @media (min-width: ${1000 / 16}em) {
      border-radius: ${toRem(3)};
      &.active,
      &:hover {
        background: ${theme.lightGray};
      }
    }
  `};
`

const MenuInner = styled.div`
  display: block;
  box-sizing: border-box;
  height: 100%;
  padding-top: ${toRem(45)};
  color: ${p => darken(0.1, p.theme.iconsColor)};
`

const LinkWrapper = styled.h5`
  display: block;
  margin: ${toRem(10)} ${toRem(40)} ${toRem(10)} ${toRem(55)};
  font-size: 1rem;
  font-weight: normal;
`

const SidebarMenu = () => (
  <MenuInner>
    {['todo'].map(title => (
      <LinkWrapper>
        <StyledLink
          to={`/__admin__/${compose(
            toLower,
            join('-'),
            split(' ')
          )(title)}`}
          activeClassName="active"
        >
          {title}
        </StyledLink>
      </LinkWrapper>
    ))}
  </MenuInner>
)

export default SidebarMenu
