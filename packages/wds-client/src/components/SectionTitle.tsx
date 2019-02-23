import React from 'react'
import styled, { css } from '../styles/styled-components'

import { toRem } from '../styles/helpers'

const SectionTitle = styled.h2<{ paddingTop: number; paddingBottom: number }>`
  font-size: 2.5rem;
  ${({ theme }) =>
    css`
      color: ${theme.primaryOrange};
    `};
  font-weight: 700;
  ${({ paddingTop = 0, paddingBottom = 0 }) =>
    css`
      padding: ${toRem(paddingTop)} 0 ${toRem(paddingBottom)};
    `};
`
export default SectionTitle
