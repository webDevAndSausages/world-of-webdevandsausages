import { useState, useEffect, useRef } from 'react'
import axios from 'axios'
import { ApiRequest, RequestFromApi } from '../models/ApiRequest'
import { config } from '../config'

const headers = {
  Accept: 'application/json',
  'wds-key': 'WDSb8bd5dbf-be5a-4cde-876a-cdc04524fd27',
  'Content-Type': 'application/json'
}

export const endpoints = {
  currentEvent: `${config.API_ROOT}events/current`,
  mailingList: `${config.MAILING_LIST_URI}participants`,
  register: (id: number) => `${config.API_ROOT}events/${id}/registrations`,
  checkRegistration: (id: number, token: string) =>
    `${config.API_ROOT}events/${id}/registrations/${token}`,
  cancelRegistration: (token: string) => `${config.API_ROOT}events/registrations/${token}`
}

type Method = 'get' | 'post' | 'delete'
// if you want data loaded when the component loads pass immediate true
// otherwise call the returned with payload and or url with params, e.g. query({payload, url})
// to trigger request
export function useApi(endpoint: string, immediate = true, method = 'get') {
  const [request, setRequestState] = useState<RequestFromApi>(ApiRequest.NOT_ASKED())
  const mountedRef = useRef(false)
  const [query, setQuery] = useState({
    endpoint,
    payload: null,
    called: false,
    method
  })

  useEffect(() => {
    mountedRef.current = true
    return () => (mountedRef.current = false)
  }, [])

  const setRequestStateSafely = (requestState: any) =>
    mountedRef.current && setRequestState(requestState)

  async function handleFetch() {
    const request = [
      endpoints[query.endpoint] || query.endpoint,
      query.payload,
      { headers }
    ].filter(v => v)
    try {
      const { data } = await axios[query.method](...request)
      return setRequestStateSafely(ApiRequest.OK({ data }))
    } catch (e) {
      return setRequestStateSafely(
        ApiRequest.NOT_OK({ error: e.response.data, status: e.response.status })
      )
    }
  }

  useEffect(() => {
    if (immediate) {
      setRequestStateSafely(ApiRequest.LOADING())
      handleFetch()
    } else if (query.called) {
      setRequestStateSafely(ApiRequest.LOADING())
      handleFetch()
    }
  }, [query])

  return {
    request,
    query: (data: { payload?: any; endpoint?: any; method?: Method }) => {
      setQuery({ ...query, called: true, ...data })
    },
    reset: () => {
      setQuery({ ...query, called: false })
      setRequestState(ApiRequest.NOT_ASKED())
    }
  }
}
