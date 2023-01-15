import { writable, derived } from 'svelte/store';
import { isValidToken } from '../utils';

export const createTokenStore = () => writable({ token: '', validationOff: false });

export const createTokenValidationStore = (tokenStore) =>
	derived(tokenStore, ($t) => {
		const error = isValidToken($t.token);
		return $t.validationOff ? null : error;
	});
