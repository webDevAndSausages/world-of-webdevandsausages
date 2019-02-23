import React from 'react'
import styled, { css } from '../styles/styled-components'

const Svg = styled.svg`
  svg {
    display: inline-block;
    path {
      fill: currentColor;
    }
  }
  ${({ color }) =>
    color &&
    css`
      color: ${color};
    `};
  ${({ width, height }) =>
    width &&
    height &&
    css`
      width: ${width};
      height: ${height};
    `};
`

export default Svg
