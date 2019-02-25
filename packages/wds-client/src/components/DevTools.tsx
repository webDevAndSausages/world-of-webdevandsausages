import styled from '../styles/styled-components'

export const MetaWrapper = styled('div')`
  padding: 1rem;

  position: fixed;
  top: 100px;
  left: 0;

  display: flex;
  flex-direction: column;

  color: white;
  background: hsla(0, 0%, 0%, 0.6);
`

export const Pre = styled('pre')`
  margin-bottom: 1rem;

  font-size: 12;
  b {
    font-weight: bold;
  }
`
