import {readable} from 'svelte/store'
import ky from 'ky'
import api from '../api'
import config from '../config'
import {Result} from '../models/Result'
import {isEvent, formatDate} from '../utils'

// if the event is passed in, e.g. ssr
// then we can skip fetching it
export const createEventStore = event =>
	readable(Result.Pending, async set => {
		if (isEvent(event)) {
			set(Result.Ok(event))
		} else {
			const response = await ky(api.currentEvent, {
				method: 'GET',
				headers: config.headers,
			})

			if (!response.ok) {
				set(Result.Error(response.statusText))
			}
			const parsed = await response.json()
			set(Result.Ok(formatDate(parsed)))
		}
	})
