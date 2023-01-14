import {writable} from 'svelte/store'

function createStore(initial) {
	const state = writable(initial)

	const toggleExpander = () =>
		state.update(state => ({
			open: !state.open,
		}))

	return {subscribe: state.subscribe, actions: {toggleExpander}}
}

export const controlsStore = createStore({open: false})
