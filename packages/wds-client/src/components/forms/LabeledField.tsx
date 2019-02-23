import React from 'react'
import styled, { css } from '../../styles/styled-components'
import { Field } from 'react-final-form'
import darken from 'polished/lib/color/darken'
import transparentize from 'polished/lib/color/transparentize'
import lighten from 'polished/lib/color/lighten'

import { toRem } from '../../styles/helpers'
import { Cell } from '../layout'
import { ThemeInterface } from '../../styles/theme'

export const baseInput = ({ theme }: { theme: ThemeInterface }) => `
  border-radius: 3px;
  color: #111;
  width: 100%;
  font-size: ${toRem(20)};
  font-weight: 400;
  padding: ${toRem(10)} ${toRem(15)};
  vertical-align: middle;
  box-sizing: border-box;
  margin: 0;
  outline: 0;
  border: 2px solid ${transparentize(0.2, theme.secondaryBlue)};
  background: #fff;
  ::placeholder {
    color: ${lighten(0.3, theme.iconsColor)};
  }
  &:hover,
  &:readonly,
  &:focus {
    outline: 0;
    background: #0b7ebc;
    color: white;
  };
  `

export const activeInput = ({ active, theme }: any) => {
  if (active) {
    return `
        border: 2px solid ${theme.secondaryBlue};
      `
  }
  return null
}

export const validInput = ({ active, valid, theme }: any) => {
  if (active && valid) {
    return `
    border: 2px solid ${theme.secondaryBlue};
  `
  }
  return null
}

export const invalidInput = ({ active, error, theme }: any) => {
  if (error) {
    return `
    border: 2px solid ${darken(0.2, theme.notificationError)};
  `
  }
  return null
}

export const Input = styled.input<any>`
  height: 52px;
  ${baseInput};
  ${activeInput};
  ${validInput};
  ${invalidInput};
  ${({ width }) =>
    width &&
    css`
      width: ${toRem(width)};
    `};
`

export const FieldWrapper = styled.div<{ row?: boolean }>`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: space-between;
  ${p =>
    p.row &&
    css`
      flex-direction: row;
      align-items: flex-start;
      justify-content: start;
    `};
`

export const LabelWrapper = styled.label`
  font-size: ${toRem(24)};
  font-weight: bold;
  line-height: 150%;
  ${({ theme }) =>
    css`
      color: ${darken(0.1, theme.primaryOrange)};
    `};
`

export const InputCell = styled(Cell)`
  padding-bottom: 15px;
`

export const Label = ({ text }: any) => <LabelWrapper>{text}</LabelWrapper>

export const Error = styled.div`
  text-align: left;
  font-weight: 700;
  color: ${({ theme }) => darken(0.1, theme.notificationError)};
  height: 24px;
  margin-top: -10px;
  padding: 0;
`

const LabeledField = ({
  name,
  label,
  type = 'text',
  placeholder,
  ...rest
}: any) => {
  // Why does this need to be reassigned to work?
  const text = label
  return (
    <InputCell {...rest}>
      <Field name={name}>
        {({ input, meta }) => (
          <div>
            <FieldWrapper>
              <Label text={text} />
              <Input
                type={type}
                {...input}
                valid={meta.valid}
                placeholder={placeholder}
                active={!!input.value.length}
                error={meta.touched && meta.error}
              />
            </FieldWrapper>
            {meta.touched && meta.error && <Error>{meta.error}</Error>}
          </div>
        )}
      </Field>
    </InputCell>
  )
}

export default LabeledField
