import React from 'react'
import styled, { css } from '../styles/styled-components'
import Provider from 'reactabular-table/dist/provider'
import Header from 'reactabular-table/dist/header'
import Body from 'reactabular-table/dist/body'
import darken from 'polished/lib/color/darken'
import lighten from 'polished/lib/color/lighten'

export const TableWrapper = styled.div`
  position: relative;
  display: flex;
  flex-direction: column;
  ${({ theme }) =>
    css`
      border-right: 1px solid ${theme.lightGray};
    `};
  * > {
    box-sizing: border-box;
  }
  color: ${p => darken(0.1, p.theme.iconsColor)};
`

export const Table = styled(Provider)`
  flex: auto 1;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  width: 100%;
  border-collapse: collapse;
  overflow: auto;
`

export const TableHead = styled(Header)`
  flex: 1 0 auto;
  display: flex;
  flex-direction: column;
  user-select: none;
  tr {
    flex: 1 0 auto;
    display: inline-flex;
    ${({ theme }) =>
      css`
        background: ${lighten(0.3, theme.lightGray)};
        border-bottom: 3px solid ${theme.lightGray};
      `};
  th,
  td {
    flex: 1 0 0px;
    white-space: nowrap;
    text-align: left;
    text-overflow: ellipsis;
    padding: 7px 5px;
    overflow: hidden;
    transition: 0.3s ease;
    transition-property: width, min-width, padding, opacity;
    ${({ theme }) =>
      css`
        border-right: 1px solid ${theme.lightGray};
      `};
    transition: box-shadow 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    box-shadow: inset 0 0 10px 10px transparent;
    &:last-child {
      border-right: 0;
    }
  }
  ${({ theme }) =>
    css`
      > th {
        border-right: 1px solid ${theme.lightGray};
      }
    `};
`

export const TableBody = styled(Body)`
  flex: 99999 1 auto;
  display: flex;
  flex-direction: column;
  overflow: auto;
  tr {
    flex: 1 0 auto;
    display: inline-flex;
    &:last-of-kind {
      ${({ theme }) =>
        css`
          border-bottom: 1px solid ${theme.lightGray};
        `};
    }
  }
  th,
  td {
    flex: 1 0 0px;
    white-space: nowrap;
    text-overflow: ellipsis;
    padding: 7px 5px;
    overflow: hidden;
    transition: 0.3s ease;
    transition-property: width, min-width, padding, opacity;
  }
  td {
    ${({ theme }) =>
      css`
        border-right: 1px solid ${theme.lightGray};
      `};
    &:last-child {
      border-right: 0;
    }
  }
`

export const Cell = styled.td<{ center: boolean; width: number }>`
  text-align: ${p => (p.center ? 'center' : 'left')};
  ${p =>
    p.width &&
    css`
      max-width: ${p.width};
    `};
`

export const ColHead = styled.th<{ center: boolean; width: number }>`
  ${p =>
    p.width &&
    css`
      max-width: ${p.width};
    `};
`
