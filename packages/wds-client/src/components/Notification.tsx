import React from 'react'
import styled, { keyframes, css } from '../styles/styled-components'
import darken from 'polished/lib/color/darken'
import R from 'ramda'
import Svg from './Svg'

const scaleIn = keyframes`
  100% {
    transform: scale(1);
		opacity: 0.8;
  }
`

const expand = keyframes`
50% {
  width: 350px;
  border-radius: 6px;
}
100% {
  min-width: 300px;
  border-radius: 4px;
  box-shadow: 0px 1px 3px 0px rgba(0,0,0,.2),
              0px 1px 1px 0px rgba(0,0,0,.14),
              0px 3px 3px -1px rgba(0,0,0,.12);
  }
`
const fadeIn = keyframes`
  0% {
		opacity: 0;
	}
	100% {
		opacity: 0.8;
	}
`

const Wrapper = styled.div<{ type: 'success' | 'error' | undefined }>`
  margin: auto;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  position: relative;
  max-width: 400px;
  height: 50px;
  margin-top: 15px;
  margin-bottom: 15px;
  background: transparent;
  transform: scale(0);
  border-radius: 50%;
  color: #f5b53f;
  opacity: 0.8;
  overflow: hidden;
  font-weight: 500;
  animation: ${scaleIn} 0.3s ease-out forwards,
    ${expand} 0.35s 0.25s ease-out forwards;
  ${({ theme, type }) => {
    switch (type) {
      case 'success': {
        return css`
          background: ${theme.flashSuccess};
          color: ${darken(0.6, theme.flashSuccess)};
          border: 2px solid ${darken(0.4, theme.flashSuccess)};
        `
      }
      case 'error': {
        return css`
          background: ${theme.notificationError};
          color: ${darken(0.6, theme.notificationError)};
          border: 2px solid ${darken(0.4, theme.notificationError)};
        `
      }
      default: {
        return css`
          background: ${theme.notificationDefault};
          color: ${darken(0.5, theme.notificationDefault)};
          border: 2px solid ${darken(0.4, theme.notificationDefault)};
        `
      }
    }
  }};
`

const NotificationText = styled.div`
  display: flex;
  align-items: center;
  padding: 0;
  font-size: 16px;
  animation: ${fadeIn} 0.65s ease-in forwards;
`

const IconWrapper = styled.div`
  display: flex;
  align-items: center;
  padding: 0 16px;
`

export const AlertIcon = ({ width = 28, height = 28, color }) => (
  <Svg
    xmlns="http://www.w3.org/2000/svg"
    width={width}
    height={height}
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
    className="feather feather-alert-circle"
    color={color}
  >
    <circle cx="12" cy="12" r="10" />
    <line x1="12" y1="8" x2="12" y2="12" />
    <line x1="12" y1="17" x2="12" y2="16" />
  </Svg>
)

export const CheckIcon = ({ width = 28, height = 28, color }: any) => (
  <Svg
    xmlns="http://www.w3.org/2000/svg"
    width={width}
    height={height}
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
    className="feather feather-check"
    color={color}
  >
    <polyline points="20 6 9 17 4 12" />
  </Svg>
)

const InfoIcon = () => (
  <Svg
    xmlns="http://www.w3.org/2000/svg"
    width="24"
    height="24"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
    className="feather feather-info"
  >
    <circle cx="12" cy="12" r="10" />
    <line x1="12" y1="16" x2="12" y2="12" />
    <line x1="12" y1="9" x2="12" y2="8" />
  </Svg>
)

const iconMap = {
  success: CheckIcon,
  error: AlertIcon,
  info: InfoIcon
}

const Notification = ({
  type = 'info',
  defaultMessage = 'no message provided',
  message,
  show
}: any) => {
  if (!show) return null
  const text = message || defaultMessage
  const Icon = iconMap[type]

  return (
    <Wrapper type={type}>
      <IconWrapper>{<Icon /> || ''}</IconWrapper>
      <NotificationText>{text}</NotificationText>
    </Wrapper>
  )
}

export default Notification
