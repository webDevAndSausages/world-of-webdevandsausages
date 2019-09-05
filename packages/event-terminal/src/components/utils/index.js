import {
	has,
	is,
	both,
	over,
	lensProp,
	propOr,
	compose,
	contains,
	lte,
	length,
	always,
	ifElse,
} from 'ramda'
import format from 'date-fns/format'
import produce from 'immer'

export const isEvent = both(is(Object), has('status'))

export const formatDate = over(lensProp('date'), date =>
	format(date, 'MMMM do, yyyy, HH:mm')
)

export const showForStatusOf = (...statuses) =>
	compose(
		s => contains(s, statuses),
		propOr({}, 'status')
	)

export const mapActions = (actions, state) =>
	Object.keys(actions).reduce((acc, actionName) => {
		acc[actionName] = payload =>
			state.update(state => produce(actions[actionName])(state, payload))
		return acc
	}, {})

export function getPixelWidthOfText(txt) {
	let ruler = document.getElementById('ruler')
	ruler.innerText = txt
	return ruler.offsetWidth
}

const commandsRegex = /^(r|register|x|cancel|c|check|h|help)$/

export const isValidCmd = value => commandsRegex.test(value)

export const normalizeCmd = cmd =>
	cmd
		.trim()
		.slice(0, 1)
		.toLowerCase()

const commands = {
	r: 'register',
	x: 'cancel',
	c: 'check',
	h: 'help',
}

export const getFullCmd = cmd => {
	const c = normalizeCmd(cmd)
	return commands[c]
}

const emailRegex = /^.+@.+\..+$/i
const isEmail = value => emailRegex.test(value)
const minLen = compose(
	lte(1),
	length
)
const createValidator = (cond, msg) => ifElse(cond, always(null), always(msg))
export const validateEmail = createValidator(isEmail, 'Invalid email')
export const validateName = createValidator(minLen, 'A little longer please')
