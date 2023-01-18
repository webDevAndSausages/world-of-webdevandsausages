import { readable } from 'svelte/store';
import ky from 'ky';
import api from '../api';
import config from '$config';
import { Result } from '../models/Result';
import { isEvent, formatEvent } from '../utils';

// if the event is passed in, e.g. ssr
// then we can skip fetching it
export const createEventStore = (event) =>
	readable(Result.Pending, async (set) => {
		if (isEvent(event)) {
			set(Result.Ok(formatEvent(event)));
		} else {
			try {
				const response = await ky(api.currentEvent, {
					method: 'GET',
					headers: config.headers
				});

				if (!response.ok) {
					set(Result.Error(response.statusText));
				}
				const parsed = await response.json();
				set(Result.Ok(formatEvent(parsed)));
			} catch (e) {
				// 404 goes here
				set(Result.None);
			}
		}
	});
