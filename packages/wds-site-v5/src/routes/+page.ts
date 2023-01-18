import type {PageData} from '$types/data';
import speakers from '../content/speakers.json';
import sponsors from '../content/sponsors.json';
import slugify from '@sindresorhus/slugify';

const createSlug = (name: string) => slugify(name, { lowercase: true, separator: '_' });

export async function load() {
	return {
		speakers: speakers.data.map((speaker) => ({
			...speaker,
			talks: speaker.talks.filter((t) => !!t.show),
			slug: createSlug(speaker.name)
		})),
		sponsors
	} satisfies PageData;
}

export const prerender = true;
