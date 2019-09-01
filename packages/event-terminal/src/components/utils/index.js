import {map} from 'rxjs/operators'
import {has, is, both, over, lensProp, propOr, compose, contains} from 'ramda'
import format from 'date-fns/format'
import produce from 'immer'

export const isEvent = both(is(Object), has('status'))

export const formatDate = map(
	over(lensProp('date'), date => format(date, 'MMMM do, yyyy, HH:mm'))
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
