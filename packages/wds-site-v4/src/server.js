import sirv from 'sirv'
import express from 'express'
import compression from 'compression'
import * as sapper from '@sapper/server'
import proxy from 'http-proxy-middleware'

const app = express()

import './tailwind.css'

const {PORT, NODE_ENV} = process.env
const dev = NODE_ENV === 'development'

app.use(
	'/api/1.0/',
	proxy({
		target: 'http://localhost:5000',
		changeOrigin: true,
		secure: false,
		xfwd: true,
		onProxyReq: proxyReq => {
			// Browsers may send Origin headers even with same-origin
			// requests. To prevent CORS issues, we have to change
			// the Origin to match the target URL.
			console.log('proxying request', proxyReq.path)
			if (proxyReq.getHeader('origin')) {
				proxyReq.setHeader('origin', 'http://localhost:5000')
			}
		},
	})
)

app.use(compression({threshold: 0}))
app.use(sirv('static', {dev}))
app.use(sapper.middleware())
app.listen(PORT, err => {
	if (err) throw err
	console.log(`> Running on localhost:${PORT}`)
})
