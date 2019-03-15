import React from 'react'
import styled, { css } from 'styled-components'
import Markdown from 'react-markdown/with-html'

const PromptLabel = () => <span className="terminal-prompt__label">$</span>

export const EventDetailLabel = styled.label`
  font-size: 1.2rem;
  font-weight: bold;
  color: ${({ theme }) => theme.primaryOrange};
  margin: 0;
  padding: 15px 0;
  line-height: 120%;
`

export const EventDetail = styled.div`
  margin: 0;
  padding-left: 1.2rem;
  line-height: 100%;
  font-size: 1.2rem;
  ${({ theme }) =>
    css`
      color: #fff;
    `};
`

const Md = styled(Markdown)`
  > p {
    margin: 0;
  }
`

export const TerminalDetail = ({
  title,
  detail
}: {
  title: string
  detail: string
}) => (
  <>
    <EventDetailLabel>
      <PromptLabel /> {title}
    </EventDetailLabel>
    <EventDetail>
      <Md source={detail} escapeHtml={false} style={{ margin: 0 }} />
    </EventDetail>
  </>
)
