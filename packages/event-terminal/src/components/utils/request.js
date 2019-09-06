
import config from '../config'
import ky from 'ky'
import {Result} from '../models/Result'

export const createRequest = (url, method, result, payload) =>
  ky(url, {
    method,
    headers: config.headers,
    body: JSON.stringify(payload),
    hooks: {
      afterResponse: [
        async (_input, _options, response) => {
          if (response.status >= 400) {
            let error = { message: 'A mysterious error occurred on the server.' }
            try {
              error = await response.json()
            } catch (e) {
              console.log("Failed to parse response")
            }
            result = Result.Failure({
              ...error,
              status: response.status,
            })
          }
        },
      ],
    },
  })