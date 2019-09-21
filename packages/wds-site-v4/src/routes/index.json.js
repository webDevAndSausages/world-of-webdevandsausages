import speakers from '../../content/speakers.json'
import slugify from '@sindresorhus/slugify'
import got from 'got'

const createSlug = name => slugify(name, {lowercase: true, separator: '_'})

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
		console.log(error)
		//=> 'Internal server error ...'
	}

	const speakersWithSlugs = speakers.data.map(speaker => ({
		...speaker,
		talks: speaker.talks.filter(t => !!t.show),
		slug: createSlug(speaker.name),
	}))

	// cache 10 min in browser and 20 min in cdn
	res.set('Cache-Control', 'public, max-age=600, s-maxage=1200')
	res.set(('Content-Type', 'application/json'))
	res.json({speakers: speakersWithSlugs, event})
}
