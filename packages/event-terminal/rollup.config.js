import svelte from 'rollup-plugin-svelte'
import resolve from 'rollup-plugin-node-resolve'
import commonjs from 'rollup-plugin-commonjs'
import livereload from 'rollup-plugin-livereload'
import {terser} from 'rollup-plugin-terser'
import pkg from './package.json'
import autoPreprocess from 'svelte-preprocess'
import postcss from "rollup-plugin-postcss";
import babel from 'rollup-plugin-babel'
import replace from 'rollup-plugin-replace'

const production = !process.env.ROLLUP_WATCH
const name = pkg.name
	.replace(/^(@\S+\/)?(svelte-)?(\S+)/, '$3')
	.replace(/^\w/, m => m.toUpperCase())
	.replace(/-\w/g, m => m[1].toUpperCase())

export default {
	input: !production ? 'src/main.js' : 'src/components/components.module.js',
	output: !production
		? {
				sourcemap: true,
				format: 'iife',
				name: 'app',
				file: 'public/bundle.js',
		  }
		: [
				{
					file: pkg.module,
					format: 'es',
					sourcemap: true,
					name,
				},
				{
					file: pkg.main,
					format: 'umd',
					sourcemap: true,
					name,
				},
		  ],
	plugins: [
		babel({
			runtimeHelpers: true,
		}),
		replace({
			ENVIRONMENT: production
				? JSON.stringify('production')
				: JSON.stringify('development'),
		}),
		svelte({
			// enable run-time checks when not in production
			dev: !production,

			/**
			 * Auto preprocess supported languages with
			 * '<template>'/'external src files' support
			 **/
			preprocess: autoPreprocess({
				postcss: true,
			}),

			css: css => {
				css.write('public/bundle.css')
			},
		}),
		postcss({
			extract: "public/utils.css"
		}),

		// If you have external dependencies installed from
		// npm, you'll most likely need these plugins. In
		// some cases you'll need additional configuration —
		// consult the documentation for details:
		// https://github.com/rollup/rollup-plugin-commonjs
		resolve(),
		commonjs({
			include: ['node_modules/**'],
		}),

		!production && livereload('public'),

		production && terser(),
	],
	watch: {
		clearScreen: false,
	},
}
