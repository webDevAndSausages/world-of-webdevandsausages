import React, { createContext, useState } from 'react'
import { createGlobalStyle } from './styles/styled-components'
import { ThemeProvider } from './styles/styled-components'
import { theme } from './styles/theme'
import { useApi } from './hooks/useApi'
import { ApiRequest, RequestFromApi } from './models/ApiRequest'
import normalize from 'polished/lib/mixins/normalize'
import { UI, Theme } from './models/UI'
import { Switch, Route } from 'react-router-dom'
import Nav from './components/nav'
import ScrollWatcher from './components/ScrollWatcher'
import { Home } from './pages/home'

const defaultUiState = {
  theme: Theme.Reversed,
  showMobileNav: false,
  isScrolled: false,
  showSidebar: false,
  isSideClosed: true
}

export const EventContext = createContext<RequestFromApi>(
  ApiRequest.NOT_ASKED()
)
export const UiContext = createContext<UI>(defaultUiState)

const GlobalStyles = createGlobalStyle`
  ${normalize()}
  font-family: museo_sans500, sans-serif;
  font-weight: 400;
`

function App() {
  const event = useApi('events', true)
  const [uiState, setUIState] = useState(defaultUiState)
  return (
    <ThemeProvider theme={theme}>
      <EventContext.Provider value={event as any}>
        <UiContext.Provider value={uiState}>
          <ScrollWatcher
            isScrolled={uiState.isScrolled}
            setIsScrolled={(isScrolled: boolean) =>
              setUIState({ ...uiState, isScrolled })
            }
          >
            <Nav
              disableRegistration={false}
              isFeedbackLinkVisible={false}
              toggleNav={() =>
                setUIState({
                  ...uiState,
                  showMobileNav: !uiState.showMobileNav
                })
              }
            />
          </ScrollWatcher>
        </UiContext.Provider>
        <GlobalStyles />
        <Switch>
          <Route path="/" component={Home} />
        </Switch>
      </EventContext.Provider>
    </ThemeProvider>
  )
}

export default App
