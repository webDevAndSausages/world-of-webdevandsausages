import React from 'react'
import styled, { css } from '../../styles/styled-components'
import { Link } from 'preact-router/match'
import darken from 'polished/lib/color/darken'

import { sections } from '../../routes/admin/AdminPanel'
import { toRem } from '../../helpers/styleHelpers'
import R from '../../helpers'

const StyledLink = styled(Link)`
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

const SidebarMenu = ({ matches }) => (
  <MenuInner>
    {sections.map(title => (
      <LinkWrapper>
        <StyledLink
          href={`/__admin__/${R.dashify(title)}`}
          activeClassName="active"
        >
          {title}
        </StyledLink>
      </LinkWrapper>
    ))}
  </MenuInner>
)

export default SidebarMenu
