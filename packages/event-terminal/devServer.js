const sirv = require('sirv')
const compress = require('compression')()
const proxy = require('http-proxy-middleware')
const cors = require('cors')
const express = require('express')

const app = express()

// Init `sirv` handler
const assets = sirv('public', {
	maxAge: 31536000, // 1Y
	immutable: true,
})

app.use(cors())
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
app.use(compress, assets)
app.listen(3000, err => {
	if (err) throw err
	console.log(`> Running on localhost:3000`)
})
