import speakers from '../content/speakers.json'
import sponsors from '../content/sponsors.json'
import slugify from '@sindresorhus/slugify'
import got from 'got'

const createSlug = name => slugify(name, {lowercase: true, separator: '_'})

/** @type {import('./$types').PageLoad} */
export async function load({setHeaders}) {
	let event = null
	try {
		const response = await got('https://webdevandsausages.org/api/1.0/events/current', {
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
	}

	const speakersWithSlugs = speakers.data.map(speaker => ({
		...speaker,
		talks: speaker.talks.filter(t => !!t.show),
		slug: createSlug(speaker.name),
	}))

  setHeaders({
    'cache-control': 'public, max-age=600, s-maxage=1200'
  })

  return {
    speakers: speakersWithSlugs,
    event,
    sponsors
  }
}
