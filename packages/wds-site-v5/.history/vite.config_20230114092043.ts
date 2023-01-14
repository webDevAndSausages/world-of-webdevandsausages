import { sveltekit } from '@sveltejs/kit/vite';
import type { UserConfig } from 'vite';

const config: UserConfig = {
	plugins: [
		sveltekit(),
	],
	vite: {
		resolve: {
			alias: resolveBaseUrl
		}
	}
	test: {
		include: ['src/**/*.{test,spec}.{js,ts}']
	}
};

export default config;
