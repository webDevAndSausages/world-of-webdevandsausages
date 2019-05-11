import { useEffect, useState } from 'react'
import { LocationState } from 'history'

type UseURLSearchParams = (search: string, param: string, target?: string) => string | undefined

function useURLSearchParam(search: string, param: string, target?: string): string | undefined {
  const [value, setParam] = useState(null)
  useEffect(() => {
    const params = new URLSearchParams(search)
    const foundParam = params.get(param)
    setParam(foundParam)
    if (foundParam && target) {
      setTimeout(() => {
        const elementToScrollTo = document.getElementById(target)
        elementToScrollTo && elementToScrollTo.scrollIntoView()
      }, 50)
    }
  }, [search])
  return value
}

export default useURLSearchParam
