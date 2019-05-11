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
import About from './pages/home/About'
import { assoc } from 'ramda'

const defaultUiState = {
  theme: Theme.Reversed,
  showMobileNav: false,
  showSidebar: false,
  isSideClosed: true,
  isTerminalExpanded: false,
  toggleTerminalSize: null,
  setTheme: null
}

export const EventContext = createContext<RequestFromApi>(ApiRequest.NOT_ASKED())
export const UiContext = createContext<UI>(defaultUiState)

const GlobalStyles = createGlobalStyle`
  ${normalize()}
  font-family: museo_sans500, sans-serif;
  font-weight: 400;
`

function App() {
  const { request } = useApi('currentEvent', true)
  const [uiState, setUIState] = useState(defaultUiState)
  const setTheme = (theme: Theme) => setUIState(assoc('theme', theme, uiState))
  const toggleTerminalSize = () => {
    setUIState(assoc('isTerminalExpanded', !uiState.isTerminalExpanded, uiState))
  }

  return (
    <ThemeProvider theme={theme}>
      <EventContext.Provider value={request}>
        <UiContext.Provider value={{ ...uiState, setTheme, toggleTerminalSize }}>
          <ScrollWatcher>
            {({ isScrolled }: { isScrolled: boolean }) => (
              <Nav
                disableRegistration={false}
                isFeedbackLinkVisible={false}
                isScrolled={isScrolled}
                toggleNav={() =>
                  setUIState({
                    ...uiState,
                    showMobileNav: !uiState.showMobileNav
                  })
                }
              />
            )}
          </ScrollWatcher>
          <GlobalStyles />
          <Switch>
            <Route
              path="/about"
              render={() => {
                return <About />
              }}
            />
            <Route path="/" render={props => <Home setTheme={setTheme} {...props} />} />
          </Switch>
        </UiContext.Provider>
      </EventContext.Provider>
    </ThemeProvider>
  )
}

export default App
