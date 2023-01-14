import {
	has,
	is,
	both,
	propOr,
	compose,
	includes,
	lte,
	length,
	always,
	ifElse,
} from 'ramda'
import {zonedTimeToUtc, utcToZonedTime, format} from 'date-fns-tz'
import add from 'date-fns/add'
import produce from 'immer'

export const isEvent = both(is(Object), has('status'))

export const timeZone = 'Europe/Helsinki'
const readablePattern = "MMMM do, yyyy, HH:mm 'GMT' XXX (z)"
const icsPattern = "yMMdd'T'HHmmss"

const toUtcDate = date => zonedTimeToUtc(date, timeZone)
const toZonedDate = date => utcToZonedTime(new Date(date), timeZone)
const formatWithZone = zonedDate =>
	format(zonedDate, readablePattern, {timeZone})

const formatIcsDate = (date, offset = 0) =>
	compose(
		date => format(add(date, {hours: offset}), icsPattern),
		date => new Date(date)
	)(date)

export const formatDate = compose(
	formatWithZone,
	toZonedDate,
	toUtcDate
)

export const formatEvent = event => ({
	...event,
	date: formatDate(event.date),
	icsNow: format(new Date(), icsPattern),
	icsStart: formatIcsDate(event.date),
	icsEnd: formatIcsDate(event.date, 3.5),
	serverDate: event.date,
})

export const showForStatusOf = (...statuses) =>
	compose(
		s => includes(s, statuses),
		propOr({}, 'status')
	)

export const mapActions = (actions, state) =>
	Object.keys(actions).reduce((acc, actionName) => {
		acc[actionName] = payload =>
			state.update(state => produce(actions[actionName])(state, payload))
		return acc
	}, {})

export const getPixelWidthOfText = id => txt => {
	let ruler = document.getElementById(id)
	ruler.innerText = txt
	return ruler.offsetWidth
}

const commandsRegex = /^(r|register|x|cancel|c|check|h|help)$/

export const isValidCmd = value =>
	is(String, value) && commandsRegex.test(value.toLowerCase())

export const normalizeCmd = cmd =>
	cmd
		.trim()
		.slice(0, 1)
		.toLowerCase()

const terminalCommands = {
	r: 'register',
	x: 'cancel',
	c: 'check',
	h: 'help',
}

const formCommands = {
	s: 'submit',
	x: 'cancel',
	r: 'reset',
}

export const getFullTerminalCmd = cmd => {
	const c = normalizeCmd(cmd)
	return terminalCommands[c]
}

export const getFullFormCmd = cmd => {
	const c = normalizeCmd(cmd)
	return formCommands[c]
}

const emailRegex = /^.+@.+\..+$/i
const isEmail = value => emailRegex.test(value)
const minLen = min =>
	compose(
		lte(min),
		length
	)
const createValidator = (cond, msg) => ifElse(cond, always(null), always(msg))
export const validateEmail = createValidator(isEmail, 'Invalid email')
export const validateName = createValidator(minLen(1), 'A little longer please')
export const isValidToken = createValidator(
	both(contai('-'), minLen(5)),
	'Invalid token format'
)

export const isString = is(String)
