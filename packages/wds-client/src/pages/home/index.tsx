import React, { useContext } from 'react'
import PageWrapper from '../../components/PageWrapper'
import { EventContext } from '../../App'
import LargeLogo from '../../components/LargeLogo'
import Separator from '../../components/Separator'
import { MailingListForm } from './MailingListForm'
import styled, { css, keyframes } from 'styled-components'
import { toRem, tablet, phone } from '../../styles/helpers'
import bgImage from '../../images/sausage-bg.svg'

const TopSection = styled.div<any>`
  padding: ${p => `${toRem(p.theme.navHeight)} ${toRem(p.theme.pagePadding)}}`};
  ${p =>
    p.isExpandedMobileNav &&
    tablet(css`
      padding-top: ${toRem(p.theme.navHeight * 1.8)};
    `)};
  ${p =>
    p.isExpandedMobileNav &&
    phone(css`
      padding-top: ${toRem(p.theme.navHeight * 2.2)};
    `)};
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.17);
  width: 100%;
  ${p => css`
    background-image: url(${bgImage}),
      linear-gradient(15deg, ${p.theme.primaryOrange}, ${'#52bdf6'});
  `};
  background-size: 60px, auto;
  background-repeat: repeat;
  margin-top: -30px;
`

export function Home() {
  const event = useContext(EventContext)
  return (
    <PageWrapper>
      <TopSection isExpandedMobileNav={false}>
        <LargeLogo />
        <Separator />
        <MailingListForm />
      </TopSection>
    </PageWrapper>
  )
}
