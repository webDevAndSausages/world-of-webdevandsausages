const production = !process.env.ROLLUP_WATCH
const purgecss = require('@fullhuman/postcss-purgecss')

module.exports = {
	plugins: [
		require('postcss-import')(),
		require('autoprefixer')(),
		require('tailwindcss'),
		production &&
			require('cssnano')({
				preset: 'default'
			}),
		production &&
			purgecss({
				content: [ './**/*.html', './**/*.svelte' ],
				defaultExtractor: (content) => content.match(/[A-Za-z0-9-_:/]+/g) || []
			})
	].filter(Boolean)
}
