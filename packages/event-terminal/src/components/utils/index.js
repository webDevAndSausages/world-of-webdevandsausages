import { has, is, both, over, lensProp, propOr, compose, contains, lte, length, always, ifElse } from 'ramda'
import { zonedTimeToUtc, utcToZonedTime, format } from 'date-fns-tz'
import produce from 'immer'

export const isEvent = both(is(Object), has('status'))

const timeZone = 'Europe/Helsinki'
const pattern = "MMMM do, yyyy, HH:mm 'GMT' XXX (z)"

const toUtcDate = (date) => zonedTimeToUtc(date, timeZone)
const toZonedDate = (date) => utcToZonedTime(new Date(date), timeZone)
const formatWithZone = (zonedDate) => format(zonedDate, pattern, { timeZone })

export const formatDate = compose(formatWithZone, toZonedDate, toUtcDate)

export const formatEvent = over(lensProp('date'), formatDate)

export const showForStatusOf = (...statuses) => compose((s) => contains(s, statuses), propOr({}, 'status'))

export const mapActions = (actions, state) =>
	Object.keys(actions).reduce((acc, actionName) => {
		acc[actionName] = (payload) => state.update((state) => produce(actions[actionName])(state, payload))
		return acc
	}, {})

export const getPixelWidthOfText = (id) => (txt) => {
	let ruler = document.getElementById(id)
	ruler.innerText = txt
	return ruler.offsetWidth
}

const commandsRegex = /^(r|register|x|cancel|c|check|h|help)$/

export const isValidCmd = (value) => is(String, value) && commandsRegex.test(value.toLowerCase())

export const normalizeCmd = (cmd) => cmd.trim().slice(0, 1).toLowerCase()

const terminalCommands = {
	r: 'register',
	x: 'cancel',
	c: 'check',
	h: 'help'
}

const formCommands = {
	s: 'submit',
	x: 'cancel',
	r: 'reset'
}

export const getFullTerminalCmd = (cmd) => {
	const c = normalizeCmd(cmd)
	return terminalCommands[c]
}

export const getFullFormCmd = (cmd) => {
	const c = normalizeCmd(cmd)
	return formCommands[c]
}

const emailRegex = /^.+@.+\..+$/i
const isEmail = (value) => emailRegex.test(value)
const minLen = (min) => compose(lte(min), length)
const createValidator = (cond, msg) => ifElse(cond, always(null), always(msg))
export const validateEmail = createValidator(isEmail, 'Invalid email')
export const validateName = createValidator(minLen(1), 'A little longer please')
export const isValidToken = createValidator(both(contains('-'), minLen(5)), 'Invalid token format')

export const isString = is(String)
