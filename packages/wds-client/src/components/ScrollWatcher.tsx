import React, { useState, useEffect } from 'react'

interface Props {
  isScrolled: boolean
  setIsScrolled: any
}

function ScrollWatcher({ children }) {
  const [isScrolled, setIsScrolled] = useState(false)
  useEffect(() => {
    const onScroll = _e => {
      const currentScrolled =
        (window.pageYOffset || document.body.scrollTop) > 0
      console.log(currentScrolled, isScrolled)
      if (currentScrolled !== isScrolled) {
        setIsScrolled(currentScrolled)
      }
    }
    // https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener#Improving_scrolling_performance_with_passive_listeners
    window.addEventListener('scroll', onScroll, {
      capture: true,
      passive: true
    })

    return () => window.removeEventListener('scroll', onScroll)
  }, [isScrolled])

  return <div>{children({ isScrolled })}</div>
}

export default ScrollWatcher
