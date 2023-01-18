import config from '$config';
import ky from 'ky';
import { Result } from '../models/Result';

const createRequest = (method) => async (url, payload) => {
	const requestConfig = {
		method,
		headers: config.headers,
		throwHttpErrors: false
	};

	if (payload) {
		requestConfig.body = JSON.stringify(payload);
	}

	const response = await ky(url, requestConfig);

	let error = {
		message: 'A mysterious error occurred on the server.'
	};

	if (!response.ok) {
		try {
			error = await response.json();
		} catch (e) {
			console.log('Failed to parse error response');
		}
		if (response.status === 404) {
			error.message = 'Resource not found.';
		}
		return Result.Failure({
			...error,
			status: response.status
		});
	}

	try {
		const parsed = await response.json();
		return Result.Ok(parsed);
	} catch (e) {
		console.log('Failed to parse response');
		return Result.Failure({ status: 500, ...error });
	}
};

export const apiGet = createRequest('GET');
export const apiPost = createRequest('POST');
export const apiDelete = createRequest('DELETE');
