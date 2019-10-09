import {writable, derived, readable} from 'svelte/store'
import {validateEmail, validateName} from '../utils'
import {evolve, always, identity} from 'ramda'
import {successAscii} from '../ascii/success'

const initialFormValues = {
	email: '',
	firstName: '',
	lastName: '',
	affiliation: '',
	validationOff: false,
}

export const initialState = {
	values: initialFormValues,
}

export const createFormValuesStore = () => writable(initialState)

const validations = {
	email: validateEmail,
	firstName: validateName,
	lastName: validateName,
	affiliation: always(null),
}

export const createValidationStore = formValuesStore =>
	derived(formValuesStore, $r => {
		const errors = evolve(validations, $r.values)
		const errorResults = Object.values(errors)
		const isValid = errorResults.filter(identity).length === 0
		return $r.validationOff ? {errors: {}, isValid: true} : {errors, isValid}
	})

const LINE_DISPLAY_DELAY = 50

export const successAsciiStore = readable('', set => {
	const successArray = successAscii.split('\n')
	function getLines(initial) {
		setTimeout(() => {
			const nextLine = initial + '\n' + successArray[0]
			set(nextLine)
			successArray.shift()
			if (successArray.length > 0) getLines(nextLine)
		}, LINE_DISPLAY_DELAY)
	}
	getLines('')
})
