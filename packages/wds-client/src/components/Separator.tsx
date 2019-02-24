import React from 'react'
import styled, { css } from '../styles/styled-components'

const Separator = styled.hr<{ orange?: boolean }>`
  width: 50%;
  border: 0;
  height: 0;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid rgba(8, 94, 140, 0.3);
  margin-top: 2rem;
  margin-bottom: 2rem;
  ${({ orange }) =>
    orange &&
    css`
      border-bottom: 1px solid rgba(237, 185, 82, 0.3);
    `};
`

export default Separator
