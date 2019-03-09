import styled, { css } from "../../styles/styled-components"
import { toRem, phone, tablet } from "../../styles/helpers"
import { theme } from "../../styles/theme"
import transparentize from "polished/lib/color/transparentize"

const inputBackground = transparentize(0.8, theme.secondaryBlue)

export const Input = styled.input<any>`
  border: 2px solid ${theme.secondaryBlue};
  background: ${inputBackground};
  border-radius: 3px;
  color: ${theme.secondaryBlue};
  width: 30%;
  font-size: ${toRem(20)};
  font-weight: 400;
  height: 52px;
  padding: ${toRem(10)} ${toRem(15)};
  vertical-align: middle;
  box-sizing: border-box;
  margin: 0;
  outline: 0;
  &:hover,
  &:readonly {
    outline: none;
    background: #0b7ebc;
  }
  &:focus,
  &:active {
    outline: none;
    background: ${theme.secondaryBlue};
  }
  /* removes chrome's yellow background */
  &,
  &:-webkit-autofill {
    -webkit-text-fill-color: #fff;
    box-shadow: 0 0 0px 1000px ${({ active }) => (active ? theme.secondaryBlue : inputBackground)}
      inset;
    -webkit-box-shadow: 0 0 0px 1000px
      ${({ active }) => (active ? theme.secondaryBlue : inputBackground)} inset;
  }
  &:-webkit-autofill:hover,
  &:-webkit-autofill:focus,
  &:-webkit-autofill:active {
    -webkit-text-fill-color: #fff;
    box-shadow: 0 0 0px 1000px ${theme.secondaryBlue} inset;
    -webkit-box-shadow: 0 0 0px 1000px ${theme.secondaryBlue} inset;
  }
  ::placeholder {
    opacity: 0.5;
  }
  ${tablet(css`
    width: 60%;
  `)};
  ${phone(css`
    width: 70%;
  `)};
  ${({ err }) =>
    err &&
    css`
      border-color: ${({ theme }) => theme.notificationError};
    `}
`
