import rem from 'polished/lib/helpers/rem'
import { css } from '../styles/styled-components'

export const toRem = (size: number | string) => rem(size, '18px')

export const phone = (inner: any) => css`
  @media (max-width: ${650 / 18}em) {
    ${inner};
  }
`

export const tablet = (inner: any) => css`
  @media (max-width: ${1000 / 18}em) {
    ${inner};
  }
`

export const desktop = (inner: any) => css`
  @media (min-width: ${1000 / 18}em) {
    ${inner};
  }
`
