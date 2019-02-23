import React, { Component } from 'react'

// TODO: was connected component, currently missing setIsScrolled callback

interface Props {
  isScrolled: boolean
  setIsScrolled: any
}

class ScrollWatcher extends Component<Props> {
  onScroll = e => {
    const isScrolled = (window.pageYOffset || document.body.scrollTop) > 0
    if (isScrolled !== this.props.isScrolled) {
      this.props.setIsScrolled(isScrolled)
    }
  }

  componentDidMount() {
    // https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener#Improving_scrolling_performance_with_passive_listeners
    window.addEventListener('scroll', this.onScroll, {
      capture: true,
      passive: true
    })
  }

  componentWillUnmount() {
    window.removeEventListener('scroll', this.onScroll)
  }

  render() {
    return <div>{this.props.children}</div>
  }
}

export default ScrollWatcher
