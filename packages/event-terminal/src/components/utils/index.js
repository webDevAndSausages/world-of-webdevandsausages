import {map} from 'rxjs/operators'
import {
	has,
	is,
	both,
	tap,
	over,
	lensProp,
	propOr,
	compose,
	contains,
} from 'ramda'
import format from 'date-fns/format'

export const isEvent = both(is(Object), has('status'))

export const formatDate = map(
	over(lensProp('date'), date => format(date, 'MMMM do, yyyy, HH:mm'))
)

export const showForStatusOf = (...statuses) =>
	compose(
		tap(v => console.log(v, statuses)),
		s => contains(s, statuses),
		propOr({}, 'status')
	)
