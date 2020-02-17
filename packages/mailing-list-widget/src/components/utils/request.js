import ky from 'ky'
import daggy from 'daggy'
import config from '../config'

const url = `${config.API_ROOT}contacts`

export const Result = daggy.taggedSum('Result', {
	None: [],
	Pending: [],
	Ok: [],
	Failure: ['error'],
})

export const postEmail = async payload => {
	const requestConfig = {
		method: 'POST',
		url,
		headers: {
			Accept: 'application/json',
			'Content-Type': 'application/json',
			...config.headers,
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

	return Result.Ok
}
