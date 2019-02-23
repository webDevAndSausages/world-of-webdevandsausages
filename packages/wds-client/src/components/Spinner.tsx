import React, { useState, useEffect, useRef } from 'react'
import styled, { css, keyframes } from '../styles/styled-components'
import { useTimeout } from '../hooks/useTimeout'

import { toRem } from '../styles/helpers'

const rotate360 = keyframes`
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
`

interface SpinnerProps {
  small?: boolean
  white?: boolean
  marginTop?: number
  absolute?: boolean
}

const StyledSpinner = styled.div<Partial<SpinnerProps>>`
  position: relative;
  margin: 5px auto;
  border: 4px solid #367db7;
  border-top-color: rgba(0, 0, 0, 0);
  border-left-color: rgba(0, 0, 0, 0);
  width: 40px;
  height: 40px;
  opacity: 0.8;
  border-radius: 50%;
  animation: ${rotate360} 0.7s infinite linear;
  ${p =>
    p.small &&
    css`
      width: 20px;
      height: 20px;
    `};
  ${p =>
    p.white &&
    css`
      border-color: #fff;
      border-top-color: rgba(0, 0, 0, 0);
      border-left-color: rgba(0, 0, 0, 0);
    `};
  ${p =>
    p.marginTop &&
    css`
      margin-top: ${toRem(`${p.marginTop}px`)};
    `};
`

const AbsoluteSpinner = styled(StyledSpinner)`
  position: absolute;
  top: 25%;
  left: 50%;
  margin: -20px 0 0 -20px;
`

const Spinner = ({ absolute, small, white, marginTop }: SpinnerProps) => {
  const [waiting, setIsWaiting] = useState(false)
  useTimeout(() => setIsWaiting(false), 800)
  if (!waiting) {
    if (absolute) {
      return (
        <AbsoluteSpinner
          className="spinner"
          small={small}
          white={white}
          marginTop={marginTop}
        />
      )
    }
    return (
      <StyledSpinner
        className="spinner"
        small={small}
        white={white}
        marginTop={marginTop}
      />
    )
  }
  return null
}

export default Spinner
