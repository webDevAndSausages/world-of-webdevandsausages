import React, { useRef, useEffect } from 'react'
import 'animated-ellipsis'

interface AnimatedEllipsisProps {
  style?: { [key: string]: any }
  marginLeft?: string
  spacing?: string
  fontSize?: string
}

export function LoadingEllipsis({
  style = {},
  marginLeft,
  spacing,
  fontSize
}: AnimatedEllipsisProps) {
  const wrapperRef = useRef(null)

  useEffect(() => {
    wrapperRef.current.animateEllipsis()
    return () => wrapperRef.current.stopAnimatingEllipsis()
  }, [])

  if (fontSize) {
    style.fontSize = fontSize
  }

  return (
    <span
      ref={wrapperRef}
      style={style}
      data-margin-left={marginLeft}
      data-spacing={spacing}
    />
  )
}
