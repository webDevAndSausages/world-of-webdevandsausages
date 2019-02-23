import React from 'react'
import styled, { css } from '../styles/styled-components'

import { toRem, tablet, phone } from '../styles/helpers'

const Panel = styled.div<{ active: boolean; width: number }>`
  min-height: 200px;
  background: #fff;
  display: none;
  align-items: start;
  width: 50%;
  margin: auto;
  padding: ${toRem(20)};
  ${p =>
    p.active &&
    css`
      display: flex;
      flex-direction: column;
    `};
  ${tablet(css`
    width: 80%;
  `)};
  ${phone(css`
    width: 100%;
  `)};
  ${({ width }) =>
    width &&
    css`
      width: ${width};
    `};
`

export default Panel
