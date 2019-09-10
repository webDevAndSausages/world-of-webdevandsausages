module.exports = {
	theme: {
		extend: {
			fontFamily: {
				term: [ 'var(--term-font-family)', 'monospace' ]
			},
			colors: {
				base: 'var(--background)',
				term: {
					'brand-1': 'var(--term-brand-primary)',
					'brand-2': 'var(--term-brand-secondary)',
					success: 'var(--term-success)',
					error: 'var(--term-error)',
					base: 'var(--term-background-color)',
					output: 'var(--term-output-text-color)'
				}
			}
		}
	},
	variants: {
		opacity: [ 'responsive', 'hover' ]
	},
	plugins: [ require('tailwindcss-elevation')([ 'hover' ]) ]
}
