import styled, { css } from '../styles/styled-components'

const PageWrapper = styled.div<{ background?: string }>`
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: top;
  align-items: center;
  text-align: center;
  color: white;
  box-sizing: border-box;
  height: 100%;
  ${({ background }) =>
    background &&
    css`
      background: ${background};
    `};
`

export default PageWrapper
