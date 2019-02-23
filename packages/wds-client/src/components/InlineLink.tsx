import React from 'react'
import styled from '../styles/styled-components'
import { Link } from 'react-router-dom'
import lighten from 'polished/lib/color/lighten'

const InlineLink = styled(Link)`
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
  color: ${p => lighten(0.2, p.theme.primaryOrange)};
`

export default InlineLink
