import React from 'react'
import styled from '../../styles/styled-components'
import { toRem } from '../../styles/helpers'
import { theme } from '../../styles/theme'

const Wrapper = styled.nav`
  display: flex;
  align-items: center;
  flex: 1 1 auto;
  padding-top: 8px;
`

const SocialLink = styled.a<any>`
  display: inline-block;
  margin-right: ${toRem(20)};
  line-height: ${toRem(theme.navHeight)};
  transition: opacity 0.2s, transform 0.2s;
  cursor: pointer;
  &:last-child {
    margin-right: 0;
  }
  &:hover,
  &:focus {
    opacity: 0.8;
  }
  &:active {
    transform: scale(0.95);
    opacity: 0.6;
  }
`

const Svg = styled.svg`
  width: ${p => toRem(Number(p.width || '24'))};
  height: ${p => toRem(Number(p.height || '24'))};
  color: ${theme.iconsColor};
  fill: ${({ fill }) => (fill ? fill : 'currentColor')};
  stroke: ${({ stroke }) => (stroke ? stroke : 'currentColor')};
`
// Social icons source feather.icons.com
// TODO: add youtube (should be added to feathers soon)

const Github = () => (
  <Svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
    <path d="M9 19c-5 1.5-5-2.5-7-3m14 6v-3.87a3.37 3.37 0 0 0-.94-2.61c3.14-.35 6.44-1.54 6.44-7A5.44 5.44 0 0 0 20 4.77 5.07 5.07 0 0 0 19.91 1S18.73.65 16 2.48a13.38 13.38 0 0 0-7 0C6.27.65 5.09 1 5.09 1A5.07 5.07 0 0 0 5 4.77a5.44 5.44 0 0 0-1.5 3.78c0 5.42 3.3 6.61 6.44 7A3.37 3.37 0 0 0 9 18.13V22" />
  </Svg>
)

const Facebook = () => (
  <Svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 24 24"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
    className="feather feather-facebook"
  >
    <path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z" />
  </Svg>
)

const Twitter = () => (
  <Svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 24 24"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
    className="feather feather-twitter"
  >
    <path d="M23 3a10.9 10.9 0 0 1-3.14 1.53 4.48 4.48 0 0 0-7.86 3v1A10.66 10.66 0 0 1 3 4s-4 9 5 13a11.64 11.64 0 0 1-7 2c9 5 20 0 20-11.5a4.5 4.5 0 0 0-.08-.83A7.72 7.72 0 0 0 23 3z" />
  </Svg>
)

const Youtube = () => (
  <Svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 24 24"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
    className="feather feather-youtube"
    fill="none"
  >
    <path d="M22.54 6.42a2.78 2.78 0 0 0-1.94-2C18.88 4 12 4 12 4s-6.88 0-8.6.46a2.78 2.78 0 0 0-1.94 2A29 29 0 0 0 1 11.75a29 29 0 0 0 .46 5.33A2.78 2.78 0 0 0 3.4 19c1.72.46 8.6.46 8.6.46s6.88 0 8.6-.46a2.78 2.78 0 0 0 1.94-2 29 29 0 0 0 .46-5.25 29 29 0 0 0-.46-5.33z" />
    <polygon
      fill="#5b5b5b"
      points="9.75 15.02 15.5 11.75 9.75 8.48 9.75 15.02"
    />
  </Svg>
)
const SocialLinks = props => (
  <Wrapper {...props}>
    <SocialLink href="https://twitter.com/webdevnsausages/">
      <Twitter />
    </SocialLink>
    <SocialLink href="https://www.facebook.com/webDevAndSausages/">
      <Facebook />
    </SocialLink>
    <SocialLink
      href="https://www.youtube.com/user/Vancampit/videos"
      fill="none"
    >
      <Youtube />
    </SocialLink>
    <SocialLink href="https://github.com/webDevAndSausages">
      <Github />
    </SocialLink>
  </Wrapper>
)

export default SocialLinks
