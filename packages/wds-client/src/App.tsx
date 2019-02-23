import React, { Component } from 'react'
import { createGlobalStyle } from './styles/styled-components'
import { ThemeProvider } from './styles/styled-components'
import { theme } from './styles/theme'
import { useApi } from './hooks/useApi'
import PageWrapper from './components/PageWrapper'

const GlobalStyles = createGlobalStyle`
  font-family: museo_sans500, sans-serif;
  font-weight: 400;
`

function App() {
  const data = useApi('events')
  return (
    <ThemeProvider theme={theme}>
      <div className="App">
        <GlobalStyles />
        <PageWrapper background="pink" style={{ height: '100%' }}>
          <pre style={{ background: 'white', color: 'purple', width: '400px' }}>
            {JSON.stringify(data, null, 2)}
          </pre>
        </PageWrapper>
      </div>
    </ThemeProvider>
  )
}

export default App
