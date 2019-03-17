import styled, { css } from '../styles/styled-components'
import { phone, tablet } from '../styles/helpers'

const autoRows = ({ minRowHeight = '20px' }) => `minmax(${minRowHeight}, auto)`

const frGetter = value =>
  typeof value === 'number' ? `repeat(${value}, 1fr)` : value

const gap = ({ gap = '8px' }) => `${gap} ${gap}`

const flow = ({ flow = 'row' }) => flow

const formatAreas = (areas: any[]) => areas.map(area => `"${area}"`).join(' ')

const Grid = styled.div<{
  flow?: string
  rows?: string | number
  areas?: string[]
  columns?: string | number
  justifyContent?: string
  alignContent?: string
  columnsTablet?: string
  columnsPhone?: string
  columnGap?: string
  rowGap?: string
}>`
  display: grid;
  grid-auto-flow: ${flow};
  grid-auto-rows: ${autoRows as any};
  ${({ rows }) => rows && `grid-template-rows: ${frGetter(rows)}`};
  grid-template-columns: ${({ columns = 12 }) => frGetter(columns)};
  grid-gap: ${gap as any};
  ${({ areas }) => areas && `grid-template-areas: ${formatAreas(areas)}`};
  ${({ columnGap }) => columnGap && `column-gap: ${columnGap}`};
  ${({ rowGap }) => rowGap && `row-gap: ${rowGap}`};
  ${({ justifyContent }) =>
    justifyContent && `justify-content: ${justifyContent}`};
  ${({ alignContent }) => alignContent && `align-content: ${alignContent}`};
  ${({ columnsTablet }) =>
    tablet(css`
      grid-template-columns: ${columnsTablet as any};
    `)};
  ${({ columnsPhone }) =>
    phone(css`
      grid-template-columns: ${columnsPhone as any};
    `)};
`

const Cell = styled.section<{
  width?: number
  height?: number
  top?: number | string
  left?: number | string
  middle?: boolean
  center?: boolean
  area?: string
}>`
  height: 100%;
  min-width: 0;
  overflow: hidden;
  align-content: space-around;
  grid-column-end: ${({ width = 1 }) => `span ${width}`};
  grid-row-end: ${({ height = 1 }) => `span ${height}`};
  ${({ left }) => left && `grid-column-start: ${left}`};
  ${({ top }) => top && `grid-row-start: ${top}`};
  ${({ center }) => center && `text-align: center`};
  ${({ area }) => area && `grid-area: ${area}`};
  ${/* prettier-ignore */
  ({ middle }) => middle && `
    display: inline-flex;
    flex-flow: column wrap;
    justify-content: center;
    justify-self: stretch;
  `};
`

const FormCell = styled(Cell)`
  ${phone(css`
    grid-column-end: span 10;
  `)}
`

export { Cell, Grid, FormCell }
