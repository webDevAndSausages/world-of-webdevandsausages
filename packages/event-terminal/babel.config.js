module.exports = {
	include: [ '**/**/*.js', '**/**/*.mjs', '**/**/*.html', '**/**/*.svelte' ],
	plugins: [
		'@babel/plugin-syntax-dynamic-import',
		[
			'@babel/plugin-transform-runtime',
			{
				useESModules: true
			}
		]
	],
	presets: [
		[
			'@babel/preset-env',
			{
				targets: '> 0.25%, not dead',
				useBuiltIns: 'usage',
				corejs: 3
			}
		]
	],
	ignore: [ 'node_modules/**' ]
}
