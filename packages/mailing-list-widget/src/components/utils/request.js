import ky from 'ky'
import daggy from 'daggy'
let version = 'tool'
if (ENVIRONMENT === 'development') {
	version = 'dev'
}

const url = `https://us-central1-wds-event-${version}.cloudfunctions.net/api/participants`

export const Result = daggy.taggedSum('Result', {
	None: [],
	Pending: [],
	Ok: ['data'],
	Failure: ['error'],
})

export const postEmail = async payload => {
	const requestConfig = {
		method: 'POST',
		url,
		headers: {
			Accept: 'application/json',
			'Content-Type': 'application/json',
		},
		throwHttpErrors: false,
	}

	if (payload) {
		requestConfig.body = JSON.stringify(payload)
	}

	const response = await ky(url, requestConfig)

	let error = {
		message: 'A mysterious error occurred on the server.',
	}

	if (!response.ok) {
		try {
			error = await response.json()
		} catch (e) {
			console.log('Failed to parse error response')
		}
		if (response.status === 404) {
			error.message = 'Resource not found.'
		}
		return Result.Failure({
			...error,
			status: response.status,
		})
	}

	try {
		const parsed = await response.json()
		return Result.Ok(parsed)
	} catch (e) {
		console.log('Failed to parse response')
		return Result.Failure({status: 500, ...error})
	}
}
