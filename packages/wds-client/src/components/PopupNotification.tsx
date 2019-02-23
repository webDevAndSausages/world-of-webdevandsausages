import React from 'react'
import styled, { css, keyframes } from '../styles/styled-components'
import transparentize from 'polished/lib/color/transparentize'
import darken from 'polished/lib/color/darken'
import lighten from 'polished/lib/color/lighten'

import { phone, tablet, toRem } from '../styles/helpers'
// import Button from './Button'

export const showAlert = keyframes`
   0% {
    transform: scale(1);
  }
  1% {
    transform: scale(0.5);
  }

  45% {
    transform: scale(1.05);
  }

  80% {
    transform: scale(0.95);
  }

  100% {
    transform: scale(1);
  }
`

export const Overlay = styled.div<{ show: boolean }>`
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
  font-size: 0; /* Remove gap between inline-block elements */
  overflow-y: auto;
  ${({ theme }) =>
    css`
      background-color: ${transparentize(0.5, theme.primaryBlue)};
    `};
  z-index: 10000;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.3s;
  &::before {
    content: ' ';
    display: inline-block;
    vertical-align: middle; /* vertical alignment of the inline element */
    height: 100%;
  }
  ${({ show }) =>
    show &&
    css`
      opacity: 1;
      pointer-events: auto;
    `};
`

export const Footer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  padding-top: 25px;
  margin: 25px auto 10px;
  padding: 13px 16px;
  border-radius: inherit;
  border-top-left-radius: 0;
  border-top-right-radius: 0;
  display: inline-block;
  position: relative;
`

export const Popup = styled.div`
  opacity: 1;
  width: 600px;
  ${tablet(css`
    width: 450px;
  `)};
  ${phone(css`
    width: 350px;
  `)};
  pointer-events: auto;
  background-color: white;
  text-align: center;
  border-radius: 5px;
  position: static;
  margin: 20px auto;
  display: inline-block;
  vertical-align: middle;
  transform: scale(1);
  transform-origin: 50% 50%;
  z-index: 10001;
  transition: transform 0.3s, opacity 0.2s;
  animation: ${showAlert} 0.3s;
  border-radius: 3px;
`

export const Text = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  line-height: 175%;
  vertical-align: top;
  text-align: left;
  margin: 10px;
  padding: 10px 25px 0;
  max-width: calc(100% - 20px);
  overflow-wrap: break-word;
  box-sizing: border-box;
  ${({ theme }) =>
    css`
      color: ${darken(0.2, theme.iconsColor)};
    `};
  font-weight: 400;
  font-size: ${toRem(20)};
`

export const Title = styled.div<any>`
  font-size: 1.8rem;
  ${({ type, theme }) => {
    if (type === 'success') {
      return css`
        color: ${theme.primaryOrange};
      `
    }
    return css`
      color: ${theme.primaryBlue};
    `
  }};
  font-weight: 600;
  text-transform: none;
  position: relative;
  display: block;
  padding: 13px 16px;
  line-height: normal;
  text-align: center;
  margin-bottom: 0px;
  &:first-child {
    margin-top: 26px;
  }
  &:not(:first-child) {
    padding-bottom: 0;
  }
  &:not(:last-child) {
    margin-bottom: 13px;
  }
`

export const ButtonText = styled.div<{ type: string }>`
  float: none;
  line-height: normal;
  text-align: center;
  display: inline-block;
  margin: 0;
  padding: 0 20px;
  ${({ theme }) =>
    css`
      color: ${darken(0.2, theme.iconsColor)};
    `};
  font-weight: 600;
  font-size: ${toRem(24)};
  ${({ type, theme }) => {
    if (type === 'success') {
      return css`
        color: ${theme.primaryOrange};
      `
    }
    return css`
      color: ${theme.primaryBlue};
    `
  }};
`

export const Svg = styled.svg<any>`
  ${({ theme }) => css`
    color: ${lighten(0.1, theme.iconsColor)};
  `};
  width: 30px;
  height: 30px;
  stroke-width: 2;
  margin: 5px;
`

const Closer = () => (
  <Svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeLinecap="round"
    strokeLinejoin="round"
    class="feather feather-x"
  >
    <line x1="18" y1="6" x2="6" y2="18" />
    <line x1="6" y1="6" x2="18" y2="18" />
  </Svg>
)

export const IconsWrapper = styled.div`
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 0;
  padding-bottom: 0;
  line-height: 100%;
`

export const ClickableCloser = ({ onClick }) => (
  <div onClick={onClick}>
    <Closer />
  </div>
)

/*
class PopupNotification extends Component {
  closePopup = () => {
    const { key, onClose, actions } = this.props
    actions.closePopupNotification({ key })
    if (onClose) onClose()
  }

  handleOutsideClick = e => {
    // ignore clicks on the component itself
    if (this.node && this.node.contains(e.target)) {
      return
    }

    this.props.show && this.closePopup()
  }

  componentWillReceiveProps({ show }) {
    const { show: wasShowing } = this.props
    if (show) {
      document.addEventListener('click', this.handleOutsideClick, false)
    } else if (wasShowing && !show) {
      document.removeEventListener('click', this.handleOutsideClick, false)
    }
  }

  componentWillUnmount() {
    document.removeEventListener('click', this.handleOutsideClick, false)
  }

  render({ type, title, show, text, textResolver }) {
    // allow different texts according to the e.g. status code
    const displayText = R.is(Function, textResolver)
      ? textResolver(this.props)
      : text
    const isSuccess = type === 'success'
    const isError = type === 'error'

    return (
      <Overlay show={show} tabIndex={-1}>
        {show ? (
          <Popup
            id={`popup-${type}`}
            className={`${show ? 'visible' : 'hidden'}`}
            innerRef={node => {
              this.node = node
            }}
            type={type}
          >
            <IconsWrapper>
              <ClickableCloser onClick={this.closePopup} />
            </IconsWrapper>
            <Title type={type}>{title || type.toUpperCase()}</Title>
            <Text>{displayText}</Text>
            <Footer>
              <Button
                light={isSuccess}
                primary={isError}
                onClick={this.closePopup}
              >
                <ButtonText id={`popup-btn-${type}`} type={type}>
                  {isSuccess ? 'Ok' : 'Close'}
                </ButtonText>
              </Button>
            </Footer>
          </Popup>
        ) : null}
      </Overlay>
    )
  }
}

const mapStateToProps = (state, { id }) => ({
  show: R.pathEq(['popup', 'key'], id, state),
  status: R.pathOr(null, ['popup', 'status'], state),
  api: R.prop('api', state)
})

export default connect(mapStateToProps)(PopupNotification)
*/
