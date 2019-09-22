import {writable, derived} from 'svelte/store'

export const SPEAKING = 'speaking'
export const SPONSORING = 'sponsoring'

function createStore() {
	const state = writable({[SPONSORING]: false, [SPEAKING]: false})
	const toggleBlurb = key =>
		state.update(s => {
			if (key !== SPEAKING && key !== SPONSORING) {
				console.error('Incorrect key used to toggle blurb: ', key)
			}
			console.log('called')
			return {...s, [key]: !s[key]}
		})
	return {subscribe: state.subscribe, toggleBlurb}
}

export const blurbStore = createStore()
export const isWideGrid = derived(
	blurbStore,
	$bs => $bs[SPONSORING] || $bs[SPEAKING]
)
