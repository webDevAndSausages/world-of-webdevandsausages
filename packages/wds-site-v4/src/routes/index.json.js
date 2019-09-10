import speakers from '../../content/speakers.json'
import events from '../../content/events.json'
import got from 'got'

export async function get(_req, res) {
	let event = null
	try {
		const response = await got('http://localhost:5000/api/1.0/events/current', {
			headers: {
				Accept: 'application/json',
				'wds-key': 'WDSb8bd5dbf-be5a-4cde-876a-cdc04524fd27',
				'Content-Type': 'application/json',
			},
			responseType: 'json',
			resolveBodyOnly: true,
		})
		event = JSON.parse(response.body)
	} catch (error) {
		console.log(error.response.body)
		//=> 'Internal server error ...'
	}
	// cache 10 min in browser and 20 min in cdn
	res.set('Cache-Control', 'public, max-age=600, s-maxage=1200')
	res.set(('Content-Type', 'application/json'))
	res.json({speakers, events, event})
}
