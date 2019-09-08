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
		'font-term': [ 'responsive', 'hover', 'focus' ]
	}
}
