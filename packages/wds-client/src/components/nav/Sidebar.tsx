import styled, { css } from '../../styles/styled-components'

import { toRem, phone } from '../../styles/helpers'

// TODO: handle toggling and addition of hamburger on small screen
const Sidebar = styled.nav<any>`
  position: fixed;
  transform: translateZ(0);
  display: block;
  z-index: 1;
  ${({ theme }) => css`
    top: ${toRem(theme.navHeight)};
    width: ${toRem(theme.sidebarWidth)};
    background: ${theme.lightGray};
  `};
  left: 0;
  bottom: 0;
  right: auto;
  box-sizing: border-box;
  color: inherit;
  overflow-y: auto;
  transition: transform 150ms ease-out;
  ${phone(css`
    ${(p: any) =>
      p.isFolded
        ? css`
            transform: translateX(${toRem(-p.theme.sidebarWidth)});
          `
        : ``};
  `)};
`

export default Sidebar
