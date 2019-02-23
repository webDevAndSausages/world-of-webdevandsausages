import React from 'react'
import styled from '../../styles/styled-components'

import Button from '../Button'

export const ButtonWrapper = styled.div`
  padding: 40px 0;
  & > button:last-of-type {
    margin-left: 15px;
  }
`

const FormButtons = ({
  loading,
  submitDisabled,
  resetDisabled,
  valid,
  minWidth = 123,
  handleReset
}: any) => (
  <ButtonWrapper>
    <Button
      type="submit"
      loading={loading}
      transparent
      disabled={submitDisabled}
      valid={valid}
      minWidth={123}
    >
      Submit
    </Button>
    <Button
      type="button"
      light
      disabled={resetDisabled}
      valid={valid}
      minWidth={minWidth}
      onClick={handleReset}
    >
      Reset
    </Button>
  </ButtonWrapper>
)

export default FormButtons
