import { writable } from 'svelte/store'

export const form = writable({ email: '', error: null })

export const machine = ({ init, states }) => {
	if (!states.hasOwnProperty(init)) throw new Error(`That isn't a state, choose a real init state!`)
	let current_state = init
	const { subscribe, set } = writable(current_state)
	const send = (event) => {
		if (!states[current_state].on[event]) {
			console.log('no such event:', event)
			return
		}

		if (!states[states[current_state].on[event]]) {
			console.log('no such state:', state)
			return
		}
		current_state = states[current_state].on[event]
		set(current_state)
	}

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
				ABORT: 'idle',
				INPUT_INVALID: 'warning'
			}
		},
		warning: {
			on: {
				VALID: 'active',
				ABORT: 'idle'
			}
		},
		loading: {
			on: {
				COMPLETE_SUCCESSFULLY: 'success',
				COMPLETE_WITH_ERROR: 'failure'
			}
		},
		success: {
			on: {
				RESET: 'idle'
			}
		},
		failure: {
			on: {
				RESET: 'idle',
				TRY_AGAIN: 'active'
			}
		}
	}
}

export const stateMachine = machine(state_config)
