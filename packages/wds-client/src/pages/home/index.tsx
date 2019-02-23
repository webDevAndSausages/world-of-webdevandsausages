import React, { useContext } from 'react'
import PageWrapper from '../../components/PageWrapper'
import { EventContext } from '../../App'

export function Home() {
  const event = useContext(EventContext)
  return (
    <PageWrapper background="pink" style={{ height: '100%' }}>
      <pre style={{ background: 'white', color: 'purple', width: '400px' }}>
        {JSON.stringify(event, null, 2)}
      </pre>
    </PageWrapper>
  )
}
