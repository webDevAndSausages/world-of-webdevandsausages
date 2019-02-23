import React from 'react'
import styled, { keyframes } from '../styles/styled-components'
import 'intersection-observer'
import Observer from 'react-intersection-observer'

const Loading = keyframes`
0% {
  background-position: -1200px 0;
}

100% {
  background-position: 1200px 0;
}
`

const Placeholder = styled.div`
  animation: ${Loading} 3s;
  animation-fill-mode: forwards;
  animation-iteration-count: infinite;
  animation-timing-function: linear;
  background: #f6f7f8;
  background: linear-gradient(
    to right,
    rgba(0, 0, 0, 0.02) 10%,
    rgba(0, 0, 0, 0.05) 30%,
    rgba(0, 0, 0, 0.02) 50%
  );
  background-size: 1200px 20px;
  border-radius: 3px;
`

const renderImg = (inView: boolean, src: string, alt: string, height: number) =>
  inView ? <img src={src} alt={alt} height={height} /> : <Placeholder />

const LazyImg = ({ src, height = 250, alt = 'image' }: any) => (
  <Observer triggerOnce>
    {(inView: any) => renderImg(inView, src, alt, height)}
  </Observer>
)

export default LazyImg
