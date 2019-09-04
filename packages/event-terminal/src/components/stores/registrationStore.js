import {writable, derived} from 'svelte/store'
import {mapActions, validateEmail, validateName} from '../utils'
import {Result} from '../models/Result'
import {evolve, always} from 'ramda'

const initialFormValues = {
	email: '',
	firstName: '',
	lastName: '',
	affiliation: '',
}

const initialState = {
	values: initialFormValues,
	result: Result.NotAsked,
}

// add components
const actions = {
	submit: state => {
		state.values = {...state.form}
		state.result = Result.Pending
	},
	reset: state => {
		state.values = initialFormValues
		state.result = Result.NotAsked
	},
}

function reduce(actions, initialState) {
	const store = writable(initialState)
	const mappedActions = mapActions(actions, store)
	return {...store, cmds: mappedActions}
}

export const registrationStore = reduce(actions, initialState)

const validations = {
	email: validateEmail,
	firstName: validateName,
	lastName: validateName,
	affiliation: always(null),
}

export const validationStore = derived(registrationStore, $r => {
	const errors = evolve(validations, $r.values)
	const errorResults = Object.values(errors)
	const isValid = errorResults.filter(Boolean).length === errorResults.length
	return {errors, isValid}
})
