import React from 'react'

// TODO: replace
/*
class EventConsumer extends Component {
  foldIntoView(event, map = v => v, renderers) {
    const {
      renderLoading,
      renderFailure,
      renderOpenEvent,
      renderOpenEventWithRegistration,
      renderClosedEvent,
      renderClosedEventWithFeedback,
      renderNoEvent
    } = renderers

    return event.case({
      NotAsked: () => this.props.actions.getEvent(),
      Loading: () => this.rendererOrNull(renderLoading),
      Failure: () => this.rendererOrNull(renderFailure),
      OpenEvent: data => this.rendererOrNull(renderOpenEvent, map(data)),
      OpenEventWithRegistration: data =>
        this.rendererOrNull(renderOpenEventWithRegistration, map(data)),
      ClosedEvent: data => this.rendererOrNull(renderClosedEvent, map(data)),
      ClosedEventWithFeedback: data =>
        this.rendererOrNull(
          [renderClosedEventWithFeedback, renderClosedEvent],
          map(data)
        ),
      NoEvent: () => this.rendererOrNull(renderNoEvent)
    })
  }

  rendererOrNull = (renderer, data) => {
    if (typeof renderer === 'function') {
      return renderer(data)
    }
    if (Array.isArray(renderer)) {
      return R.reduceWhile(
        R.isNil,
        (acc, r) => acc || this.rendererOrNull(r, data),
        null,
        renderer
      )
    }
    return null
  }

  render({ event, map, ...rest }) {
    return this.foldIntoView(event, map, rest)
  }
}

export default connect(state => ({ event: state.event }))(EventConsumer)
*/
