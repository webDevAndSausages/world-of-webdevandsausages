module.exports = {
	theme: {
		fontFamily: {
			term: ['var(--term-font-family)', 'monospace'],
		},
		colors: {
			term: {
				'brand-1': 'var(--term-brand-primary)',
				'brand-2': 'var(--term-brand-secondary)',
				base: 'var(--term-background-color)',
				text: 'var(--term-detail-text-color)',
			},
		},
	},
	variants: {
		'font-term': ['responsive', 'hover', 'focus'],
	},
}
