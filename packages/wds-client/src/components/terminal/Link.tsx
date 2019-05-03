import styled, { css } from '../../styles/styled-components'
import lighten from 'polished/lib/color/lighten'
import transparentize from 'polished/lib/color/transparentize'

export const linkStyles = css`
  color: ${({ theme }) => lighten(0.1, theme.terminalLinkColor)};
  border-bottom: 1px ${({ theme }) => theme.terminalLinkColor} dashed;
  ${({ theme }) =>
    css`
      background-color: ${transparentize(0.8, theme.primaryOrange)};
    `};
`

export const TerminalLink = styled.span`
  ${linkStyles}
`
