import {readable} from 'svelte/store'
import {of} from 'rxjs'
import {ajax} from 'rxjs/ajax'
import {startWith, catchError, map, tap, pluck} from 'rxjs/operators'
import config from './config'
import daggy from 'daggy'

const headers = {
	Accept: 'application/json',
	'wds-key': 'WDSb8bd5dbf-be5a-4cde-876a-cdc04524fd27',
	'Content-Type': 'application/json',
	crossDomain: true,
	withCredentials: true,
}

const Result = daggy.taggedSum('Result', {
	NotAsked: [],
	Pending: [],
	Success: ['data'],
	Failure: ['error'],
})

export const event = readable(Result.NotAsked, async set => {
	const source = ajax({
		url: `${config.API_ROOT}events/current`,
		headers,
	}).pipe(
		pluck('response'),
		tap(console.log),
		map(v => Result.Success(v)),
		catchError(error => {
			console.log(error)
			return of(Result.Failure(error))
		}),
		startWith(Result.Pending)
	)

	const subscription = source.subscribe(v => {
		set(v)
	})

	return () => subscription.dispose()
})
