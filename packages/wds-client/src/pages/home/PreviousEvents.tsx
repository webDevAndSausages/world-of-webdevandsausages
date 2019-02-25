import React from 'react'
import styled from 'styled-components'
import darken from 'polished/lib/color/darken'

const events = require('../../events.json')
import { theme } from '../../styles/theme'
import { Grid, Cell } from '../../components/layout'
import LazyImg from '../../components/LazyImg'
import SectionTitle from '../../components/SectionTitle'

const Wrapper = styled.div`
  min-height: 20vh;
  width: 100%;
  background: #fff;
`

const EventPanelTitle = styled.div`
  margin: 0;
  padding: 5px 0 0;
  color: ${darken(0.1, theme.primaryOrange)};
  line-height: 120%;
  text-align: center;
`

const MeetupDetails = styled.small`
  padding: 0;
  line-height: 100%;
  color: ${theme.subduedTexTColor};
  padding: 0 0 3px;
`

const Panel = styled(Cell)`
  padding-left: 10px;
  padding-bottom: 0;
  margin-bottom: 0;
  & > a > img {
    box-shadow: 0 3px 6px rgba(0, 0, 0, 0.16), 0 3px 6px rgba(0, 0, 0, 0.23);
  }
  line-height: 110%;
`

const StyledLink = styled.a`
  padding-bottom: 1px;
  text-decoration: none;
  &:hover {
    background: lightgray;
  }
  padding: 3px 0 0;
`

const EventPanel = ({ youtubeId, title, titleLink, details, startFrom }) => {
  const speakersAndMeetup = details.split('-')
  const start = startFrom ? `#t=${startFrom}` : ''
  return (
    <Panel middle>
      <a href={`https://youtu.be/${youtubeId}${start}`}>
        <LazyImg
          height={180}
          src={`https://img.youtube.com/vi/${youtubeId}/mqdefault.jpg`}
          alt="Youtube video"
        />
      </a>{' '}
      {title && !titleLink && <EventPanelTitle>{title}</EventPanelTitle>}
      {title && titleLink && (
        <StyledLink href={`${titleLink}${start}`}>
          <EventPanelTitle>{title}</EventPanelTitle>
        </StyledLink>
      )}
      <MeetupDetails>{speakersAndMeetup[1]}</MeetupDetails>
    </Panel>
  )
}

const PreviousEvents = () => (
  <Wrapper>
    <SectionTitle paddingTop={20} paddingBottom={40}>
      Previous Events
    </SectionTitle>
    <Grid columns="repeat(auto-fit,minmax(330px,1fr))">
      {events.talks.map(t => (
        <EventPanel
          title={t.title}
          titleLink={t.titleLink}
          details={t.details}
          youtubeId={t.youtubeId}
          startFrom={t.startFrom}
        />
      ))}
    </Grid>
  </Wrapper>
)

export default PreviousEvents
