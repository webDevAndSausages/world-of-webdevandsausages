import { useEffect, useRef } from 'react'

export function useTimeout(callback: Function, delay: number) {
  const savedCallback = useRef<Function | undefined>()

  // Remember the latest callback.
  useEffect(() => {
    savedCallback.current = callback
  }, [callback])

  // Set up the interval.
  useEffect(() => {
    function tick() {
      savedCallback.current()
    }
    if (delay !== null) {
      let id = setTimeout(tick, delay)
      return () => clearTimeout(id)
    }
  }, [delay])
}
