import React from 'react'
import styled, { css } from 'styled-components'

import { phone, tablet } from '../../styles/helpers'
import { Grid, Cell } from '../../components/layout'
import LazyImg from '../../components/LazyImg'
import SectionTitle from '../../components/SectionTitle'
import stickers from '../../images/stickers.png'

const Wrapper = styled.div`
  min-height: 20vh;
  width: 80%;
  background: #fff;
  margin-bottom: 4rem;
`

export const MerchandiseWrapper = styled.article`
  text-align: left;
  margin: auto;
  padding-top: 1rem;
  width: 60%;
  @media (max-width: ${1600 / 18}em) {
    width: 70%;
  }
  ${tablet(css`
    width: 80%;
  `)};
  ${phone(css`
    width: 90%;
  `)};
  ${({ theme }) =>
    css`
      color: ${theme.primaryOrange};
    `};
`

const ImgCell = styled(Cell)`
  padding-bottom: 0;
  margin-bottom: 0;
`

const MERCH = [
  'stickers',
  'hat',
  'cup',
  'shirt',
  'pipo',
  'mugs',
  'beer',
  'hoodie',
  'halloween_stuff'
]

const Merchandise = () => (
  <Wrapper>
    <SectionTitle paddingBottom={40}>Goodies</SectionTitle>
    <Grid columns="repeat(auto-fit,minmax(350px,1fr))">
      {MERCH.map(item => (
        <ImgCell key={item}>
          <LazyImg
            src={`/merchandise/${item}.jpg`}
            alt={item}
            height={item === 'beer' ? 250 : undefined}
          />
        </ImgCell>
      ))}
    </Grid>
  </Wrapper>
)

export default Merchandise
