import React from 'react'
/*
import { Router } from 'preact-router'
import styled, { ThemeProvider } from '../../styles/styled-components'
import { connect } from '../preact-smitty'
import R from '../helpers'
import isWithinRange from 'date-fns/is_within_range'

import Nav from './nav'
import Home from '../routes/home'
import About from '../routes/about'
import NotFound from '../routes/notfound'
import Registration from 'async!../routes/registration'
import Feedback from '../routes/feedback'
import Admin from 'async!../routes/admin'
import ScrollWatcher from './ScrollWatcher'

import { theme } from '../style/theme'

const Main = styled.main`
  font-family: museo_sans500, sans-serif;
  font-weight: 400;
`

class App extends Component {

  handleRoute = e => {
    if (this.currentUrl !== e.url) {
      document.body.scrollTop = 0
    }

    this.currentUrl = e.url
    const {
      reverseTheme,
      actions: { changeTheme }
    } = this.props

    if (this.currentUrl.includes('registration')) {
      changeTheme('reverse')
    } else if (this.currentUrl.includes('about')) {
      changeTheme('reverse')
    } else if (reverseTheme) {
      changeTheme('standard')
    }
  }

  componentDidMount() {
    this.props.actions.get({
      key: 'latestEvent',
      resource: 'latestEvent'
    })
  }

  //TODO: conditional rendering is broken https://github.com/developit/preact-router/issues/178
  //when fixed, hide feedback route
  render({ latestEvent, loadingEvent, isRegistrationOpen, isFeedbackOpen }) {
    return (
      <ThemeProvider theme={theme}>
        <Main id="app">
          <ScrollWatcher>
            <Nav
              disableRegistration={!isRegistrationOpen || loadingEvent}
              isFeedbackLinkVisible={isFeedbackOpen && !loadingEvent}
            />
          </ScrollWatcher>
          <Router onChange={this.handleRoute}>
            <Home path="/" />
            <About path="/about/" />
            <Registration
              path="/registration/"
              event={latestEvent}
              loadingEvent={loadingEvent}
              isRegistrationOpen={isRegistrationOpen}
            />
            <Feedback path="/feedback/" />
            <Admin path="/__admin__/:section?" />
            <NotFound default />
          </Router>
        </Main>
      </ThemeProvider>
    )
  }
}

const getIsRegistrationOpen = event => {
  if (event.registrationOpens) {
    const endDate = event.registrationCloses
      ? event.registrationCloses
      : new Date(8640000000000000)
    return isWithinRange(new Date(), event.registrationOpens, endDate)
  }
  return false
}

const eventPath = ['api', 'latestEvent']

const mapStateToProps = state => {
  const latestEvent = R.pathOr(
    {},
    eventPath.concat(['data', 'currentEvent']),
    state
  )
  const loadingEvent = R.pathEq(eventPath.concat(['status']), 'started', state)
  const isRegistrationOpen = getIsRegistrationOpen(latestEvent)
  const isFeedbackOpen = R.pathEq(
    eventPath.concat(['data', 'feedbackOpen']),
    true,
    state
  )
  const reverseTheme = R.pathEq(['ui', 'theme'], 'reverse', state)
  return {
    latestEvent,
    loadingEvent,
    isFeedbackOpen,
    isRegistrationOpen,
    reverseTheme
  }
}

export default connect(mapStateToProps)(App)
*/
