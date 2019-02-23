import styled from '../styles/styled-components'

const Logo = styled.img`
  max-width: 100%;
  height: auto;
  box-shadow: 0px 0px 161px -23px rgba(0, 0, 0, 0.75);
  border-radius: 100%;
  margin-top: 50px;
`

const LargeLogo = () => <Logo src="assets/wds-logo-round.svg" />

export default LargeLogo
