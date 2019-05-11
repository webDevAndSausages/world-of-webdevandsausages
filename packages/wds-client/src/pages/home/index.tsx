import React, { useContext, useEffect } from 'react'
import styled, { css } from 'styled-components'
import { toRem, tablet, phone } from '../../styles/helpers'

import PageWrapper from '../../components/PageWrapper'
import { EventContext } from '../../App'
import LargeLogo from '../../components/LargeLogo'
import Separator from '../../components/Separator'
import Footer from '../../components/Footer'
import Merchandise from './Merchandise'
import PreviousEvents from './PreviousEvents'
import Event from './CurrentEventTerminal'
import { MailingListForm } from './MailingListForm'
import SectionTitle from '../../components/SectionTitle'

import bgImage from '../../images/sausage-bg.svg'
import { Theme } from '../../models/UI'
import { mapRequestToEvent } from '../../models/Event'
import { RouteComponentProps } from 'react-router'

import useURLSearchParam from '../../hooks/useURLSearchParam'

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

const PreviousEventsWrapper = styled.section`
  background: #fff;
  min-height: 50vh;
  width: 100%;
  padding-bottom: 5rem;
`

const CurrentEventWrapper = styled.section`
  width: 100%;
  padding: 2rem 0 3rem;
`

interface HomeProps extends RouteComponentProps {
  setTheme: Function
}

export function Home({ setTheme, location }: HomeProps) {
  const event = useContext(EventContext)
  useEffect(() => {
    setTheme(Theme.Standard)
  }, [])

  const checkToken = useURLSearchParam(location.search, 'check', 'current-event-console')
  const cancelToken = useURLSearchParam(location.search, 'cancel', 'current-event-console')

  return (
    <PageWrapper>
      <TopSection isExpandedMobileNav={false}>
        <LargeLogo />
        <Separator />
        <MailingListForm />
      </TopSection>
      <SectionTitle>Our Next Meetup</SectionTitle>
      <CurrentEventWrapper>
        <Event {...mapRequestToEvent(event)} checkToken={checkToken} cancelToken={cancelToken} />
      </CurrentEventWrapper>
      <PreviousEventsWrapper>
        <PreviousEvents />
      </PreviousEventsWrapper>
      <Separator orange />
      <Merchandise />
      <Footer color="primaryBlue" />
    </PageWrapper>
  )
}
