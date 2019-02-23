import React from 'react'
import styled, { css } from '../styles/styled-components'
import { Link } from 'react-router-dom'

import { toRem } from '../styles/helpers'
import Spinner from './Spinner'
import opacify from 'polished/lib/color/opacify'
import lighten from 'polished/lib/color/lighten'
import transparentize from 'polished/lib/color/transparentize'

const StyledButton = styled.button<any>`
  display: inline-block;
  zoom: 1;
  line-height: normal;
  white-space: nowrap;
  vertical-align: middle;
  text-align: center;
  cursor: pointer;
  -webkit-user-drag: none;
  user-select: none;
  box-sizing: border-box;
  background: transparent;
  border-radius: 2px;
  border: 2px solid #fff;
  height: 52px;
  font-size: 1.7em;
  font-weight: 470;
  color: #fff;
  padding: 2px 17px 4px 17px;
  margin: 0 4px;
  box-shadow: none;
  &:focus {
    outline: none;
    box-shadow: 0px 0px 0px 1px white, 0px 0px 0px 3px rgba(43, 114, 165, 0.29);
  }
  ${p =>
    p.primary &&
    css`
      &:hover,
      &:disabled {
        background: ${transparentize(0.5, p.theme.primaryBlue)};
      }
      &:disabled {
        background: ${opacify(0.1, p.theme.subduedTexTColor)};
        cursor: not-allowed;
      }
      border: 2px solid ${p.theme.primaryBlue};
      background: transparent;
      color: ${p.theme.primaryBlue};
      &:hover {
        background: ${transparentize(0.8, p.theme.primaryBlue)};
        color: ${p.theme.primaryBlue};
      }
      &:disabled {
        background: ${transparentize(0.6, p.theme.primaryBlue)};
        color: ${transparentize(0.3, p.theme.primaryBlue)};
        cursor: not-allowed;
      }
    `};
  ${p =>
    p.secondary &&
    css`
      &:hover,
      &:disabled {
        background: ${transparentize(0.5, p.theme.secondaryBlue)};
      }
      &:disabled {
        background: ${opacify(0.1, p.theme.subduedTexTColor)};
        cursor: not-allowed;
      }
      border: 2px solid ${p.theme.secondaryBlue};
      background: transparent;
      color: ${p.theme.secondaryBlue};
      &:hover {
        background: ${transparentize(0.8, p.theme.secondaryBlue)};
        color: ${p.theme.secondaryBlue};
      }
      &:disabled {
        background: ${transparentize(0.6, p.theme.secondaryBlue)};
        color: ${transparentize(0.3, p.theme.secondaryBlue)};
        cursor: not-allowed;
      }
    `};
  ${p =>
    p.valid &&
    css`
      background: ${p.theme.secondaryBlue};
      color: #fff;
      &:hover {
        background: ${opacify(0.1, p.theme.secondaryBlue)};
        color: #fff;
      }
    `};
  ${p =>
    p.transparent &&
    css`
      background: transparent;
      border-color: ${p.theme.secondaryBlue};
      color: ${p.theme.secondaryBlue};
      &:disabled,
      &:hover {
        border-color: ${lighten(0.1, p.theme.secondaryBlue)};
        color: ${lighten(0.1, p.theme.secondaryBlue)};
        background: transparent;
      }
    `};
  ${p =>
    p.light &&
    css`
      background: transparent;
      border-color: ${p.theme.primaryOrange};
      color: ${p.theme.primaryOrange};
      &:disabled,
      &:hover {
        border-color: ${lighten(0.1, p.theme.primaryOrange)};
        color: ${lighten(0.1, p.theme.primaryOrange)};
        background: transparent;
      }
    `};
  ${props =>
    props.transparent &&
    props.valid &&
    css`
      &:hover {
        background: transparent;
      }
    `};
  ${props =>
    props.minWidth &&
    css`
      min-width: ${props.minWidth}px;
    `};
  ${p =>
    p.big &&
    css`
      padding: ${toRem(24)} ${toRem(30)};
      height: 92px;
      font-size: 2em;
      font-weight: bold;
      border-width: 5px;
    `};
`

const Button = ({
  type = 'button',
  id,
  loading,
  primary,
  children,
  white,
  ...rest
}: any) => (
  <StyledButton type={type} id={id} primary={primary} {...rest}>
    {!loading && children}
    {loading && <Spinner small white={white} />}
  </StyledButton>
)

export const ButtonLink = ({
  type = 'button',
  id,
  loading,
  primary,
  children,
  href,
  ...rest
}: any) => (
  <Link to={href}>
    <StyledButton type={type} id={id} primary={primary} {...rest}>
      {!loading && children}
      {loading && <Spinner small />}
    </StyledButton>
  </Link>
)

export default Button
