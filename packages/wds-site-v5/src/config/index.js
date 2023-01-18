import { PUBLIC_WDS_API_URL, PUBLIC_WDS_API_KEY } from '$env/static/public';

export default {
	API_ROOT: PUBLIC_WDS_API_URL,
	headers: {
		Accept: 'application/json',
		'wds-key': PUBLIC_WDS_API_KEY,
		'Content-Type': 'application/json'
	}
};
