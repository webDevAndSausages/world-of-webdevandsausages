import { sveltekit } from '@sveltejs/kit/vite';
import type { UserConfig } from 'vite';

const config: UserConfig = {
	plugins: [
		sveltekit(),
	],
	theme: {
		extend: {
			fontFamily: {
				term: ['var(--term-font-family)', 'monospace'],
			},
			colors: {
				background: 'var(--background)',
				base: 'var(--text-base-color)',
				'mailing-list': 'var(--mailing-list-join-background)',
				term: {
					'brand-1': 'var(--term-brand-primary)',
					'brand-2': 'var(--term-brand-secondary)',
					success: 'var(--term-success)',
					error: 'var(--term-error)',
					background: 'var(--term-background-color)',
					output: 'var(--term-output-text-color)',
				},
			},
		},
	},
	test: {
		include: ['src/**/*.{test,spec}.{js,ts}']
	}
};

export default config;
