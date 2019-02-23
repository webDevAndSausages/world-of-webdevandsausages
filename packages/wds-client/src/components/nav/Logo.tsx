import styled from '../../styles/styled-components'
import { Link } from 'react-router-dom'
import { toRem } from '../../styles/helpers'

const MainLogo = styled.img`
  position: absolute;
  left: 0;
  width: ${toRem(130)};
  height: ${toRem(130)};
  top: ${toRem(-28)};
  z-index: 80;
`
const Logo = () => (
  <Link to="/">
    <MainLogo
      className="logo bounceDown"
      src="../../assets/logo.svg"
      alt="Web Dev &amp; Sausages"
    />
  </Link>
)

export default Logo
