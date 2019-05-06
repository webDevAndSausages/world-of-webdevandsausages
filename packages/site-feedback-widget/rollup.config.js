import svelte from 'rollup-plugin-svelte'
import resolve from 'rollup-plugin-node-resolve'
import replace from 'rollup-plugin-replace'
import commonjs from 'rollup-plugin-commonjs'
import serve from 'rollup-plugin-serve'
import livereload from 'rollup-plugin-livereload'
import { terser } from 'rollup-plugin-terser'

const dev = process.env.ROLLUP_WATCH

export default {
  input: 'src/main.js',
  output: {
    sourcemap: true,
    format: 'iife',
    name: 'app',
    file: 'public/sfw-bundle.js'
  },
  plugins: [
    svelte({
      // enable run-time checks when not in production
      dev,
      // we'll extract any component CSS out into
      // a separate file — better for performance
      css: css => {
        css.write('public/sfw-bundle.css')
      }
    }),

    // If you have external dependencies installed from
    // npm, you'll most likely need these plugins. In
    // some cases you'll need additional configuration —
    // consult the documentation for details:
    // https://github.com/rollup/rollup-plugin-commonjs
    resolve(),
    commonjs(),
    replace({
      'process.env.NODE_ENV': dev ? JSON.stringify('development') : JSON.stringify('production')
    }),

    // Watch the `public` directory and refresh the
    // browser on changes when not in production
    dev && livereload('public'),
    dev &&
      serve({
        open: true,
        contentBase: 'public',
        port: 5000
      }),

    // If we're building for production (npm run build
    // instead of npm run dev), minify
    !dev && terser()
  ]
}
