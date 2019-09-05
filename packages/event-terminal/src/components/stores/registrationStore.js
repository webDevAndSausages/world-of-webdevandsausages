import {writable, derived} from 'svelte/store'
import {validateEmail, validateName} from '../utils'
import {Result} from '../models/Result'
import {evolve, always} from 'ramda'

const initialFormValues = {
	email: '',
	firstName: '',
	lastName: '',
	affiliation: '',
}

export const initialState = {
	values: initialFormValues,
	result: Result.NotAsked,
}

export const registrationStore = writable(initialState)

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
