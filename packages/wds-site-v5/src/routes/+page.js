import speakers from '../content/speakers.json';
import sponsors from '../content/sponsors.json';
import slugify from '@sindresorhus/slugify';
import { PUBLIC_WDS_API_URL, PUBLIC_WDS_API_KEY } from '$env/static/public';

const createSlug = (name) => slugify(name, { lowercase: true, separator: '_' });

/** @type {import('./$types').PageLoad} */
export async function load({ setHeaders, fetch }) {
	let event = null;
	try {
		const response = await fetch(PUBLIC_WDS_API_URL + '/events/current', {
			headers: {
				Accept: 'application/json',
				'wds-key': PUBLIC_WDS_API_KEY
			}
		});
		if (response.ok) {
			event = await response.json();
		}
	} catch (error) {}

	const speakersWithSlugs = speakers.data.map((speaker) => ({
		...speaker,
		talks: speaker.talks.filter((t) => !!t.show),
		slug: createSlug(speaker.name)
	}));

	setHeaders({
		'cache-control': 'public, max-age=600, s-maxage=1200'
	});

	return {
		speakers: speakersWithSlugs,
		currentEvent: event,
		sponsors
	};
}
