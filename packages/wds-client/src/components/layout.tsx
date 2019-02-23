import styled, { css } from '../styles/styled-components'
import { phone, tablet } from '../styles/helpers'

const autoRows = ({ minRowHeight = '20px' }) => `minmax(${minRowHeight}, auto)`

const columns = ({ columns = 12 }) =>
  typeof columns === 'number' ? `repeat(${columns}, 1fr)` : columns

const gap = ({ gap = '8px' }) => `${gap} ${gap}`

const flow = ({ flow = 'row' }) => flow

const formatAreas = (areas: any[]) => areas.map(area => `"${area}"`).join(' ')

const Grid = styled.div<{
  flow: string
  rows: string
  areas: any
  justifyContent: boolean
  alignContent: boolean
  columnsTablet: boolean
  columnsPhone: boolean
}>`
  display: grid;
  grid-auto-flow: ${flow};
  grid-auto-rows: ${autoRows as any};
  ${({ rows }) => rows && `grid-template-rows: ${rows}`};
  grid-template-columns: ${columns as any};
  grid-gap: ${gap as any};
  ${({ areas }) => areas && `grid-template-areas: ${formatAreas(areas)}`};
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

const Cell = styled.section<any>`
  height: 100%;
  min-width: 0;
  overflow: hidden;
  align-content: space-around;
  grid-column-end: ${({ width = 1 }) => `span ${width}`};
  grid-row-end: ${({ height = 1 }) => `span ${height}`};
  ${({ left }) => left && `grid-column-start: ${left}`};
  ${({ top }) => top && `grid-row-start: ${top}`};
  ${({ center }) => center && `text-align: center`};
  ${({ textLeft }) => textLeft && `text-align: left`};
  ${({ area }) => area && `grid-area: ${area}`};
  ${/* prettier-ignore */
  ({ middle }) => middle && `
    display: inline-flex;
    flex-flow: column wrap;
    justify-content: center;
    justify-self: stretch;
  `};
`

export { Cell, Grid }
