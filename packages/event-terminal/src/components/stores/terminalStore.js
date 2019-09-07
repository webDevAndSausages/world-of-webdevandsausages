import {writable} from 'svelte/store'
import {mapActions} from '../utils'
import Wait from '../Wait.svelte'
import Help from '../Help.svelte'
import Invalid from '../Invalid.svelte'
import Registration from '../Registration.svelte'
import Cancellation from '../Cancellation.svelte'
import Check from '../Check.svelte'

const formComponentMap = {
	Registration,
	Cancellation,
	Check
}

const initialState = {
	currentIdx: 0,
	history: [Wait],
	cmd: [],
	token: null,
}

// add components
const actions = {
	wait: state => {
		state.history.push(Wait)
		state.currentIdx++
	},
	register: state => {
		state.history.push(Registration)
		state.currentIdx++
	},
	cancel: (state, payload) => {
		state.history.push(Cancellation)
		state.currentIdx++
		if (payload) {
			state.token = payload.token
		}
	},
	check: (state, payload) => {
		state.history.push(Check)
		state.currentIdx++
		if (payload) {
			state.token = payload.token
		}
	},
	help: state => {
		state.history.push(Help)
		state.history.push(Wait)
		state.currentIdx += 2
	},
	invalid: (state, {cmd}) => {
		state.history.push(Invalid)
		state.history.push(Wait)
		state.cmd = cmd
		state.currentIdx += 2
	},
	reset: (state, {component}) => {
		state.history.push(formComponentMap[component])
		state.currentIdx++
	}
}

function reduce(actions, initialState) {
	const state = writable(initialState)
	const mappedActions = mapActions(actions, state)
	return {subscribe: state.subscribe, cmds: mappedActions}
}

export const terminalStore = reduce(actions, initialState)
