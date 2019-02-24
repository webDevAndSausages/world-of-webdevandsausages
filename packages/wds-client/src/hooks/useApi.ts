import { useState, useEffect, useRef } from 'react'
import axios from 'axios'
import { ApiRequest, Request } from '../models/ApiRequest'

const headers = {
  // add secrets config
  'wds-key': 'WDSb8bd5dbf-be5a-4cde-876a-cdc04524fd27',
  'Content-Type': 'application/json'
}

const API_ROOT = process.env.REACT_APP_API_ROOT

export function useApi(endpoint: string) {
  const [request, setRequestState] = useState<Request>(ApiRequest.NOT_ASKED())
  const mountedRef = useRef(false)

  useEffect(() => {
    mountedRef.current = true
    return () => (mountedRef.current = false)
  }, [])

  const setRequestStateSafely = (requestState: any) =>
    mountedRef.current && setRequestState(requestState)

  async function handleFetch() {
    try {
      const { data } = await axios.get(`${API_ROOT}${endpoint}`, { headers })
      return setRequestStateSafely(ApiRequest.OK({ data }))
    } catch (e) {
      return setRequestStateSafely(ApiRequest.NOT_OK({ error: e.message }))
    }
  }

  useEffect(() => {
    setRequestStateSafely(ApiRequest.LOADING())
    handleFetch()
  }, [])

  return request
}
