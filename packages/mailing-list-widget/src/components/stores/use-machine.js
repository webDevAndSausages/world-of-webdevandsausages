import { writable } from 'svelte/store'

export const form = writable({ email: '', error: null })

export const machine = ({ init, states }) => {
	if (!states.hasOwnProperty(init)) throw new Error(`That isn't a state, choose a real init state!`)
	let current_state = init
	const { subscribe, set } = writable(current_state)
	console.log(states)
	const send = (event) => {
		console.log(states, event)
		if (!states[current_state].on[event]) {
			console.log('no such event')
			return
		}

		if (!states[states[current_state].on[event]]) {
			console.log('no such state')
			return
		}
		current_state = states[current_state].on[event]
		set(current_state)
	}

	console.log(current_state)

	return {
		subscribe,
		send
	}
}

const state_config = {
	init: 'idle',
	states: {
		idle: {
			on: {
				START: 'active'
			}
		},
		active: {
			on: {
				SUBMIT: 'loading',
				QUIT: 'idle',
				INVALID: 'active'
			}
		},
		warning: {
			on: {
				VALID: 'active'
			}
		},
		loading: {
			on: {
				COMPLETE_SUCCESSFULLY: 'success',
				COMPLETE_WITH_ERROR: 'error'
			}
		},
		successMessage: {},
		errorMessage: {
			on: {
				TRY_AGAIN: 'active'
			}
		}
	}
}

export const stateMachine = machine(state_config)
