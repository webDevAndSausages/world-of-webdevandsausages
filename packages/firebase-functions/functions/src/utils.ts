import { create, env } from 'sanctuary'
const { env: flutureEnv } = require('fluture-sanctuary-types')
import {
  has,
  either,
  isNil,
  compose,
  not,
  assoc,
  evolve,
  identity,
  find,
  propEq,
  both,
  findIndex,
  equals,
  gt,
  propOr,
  flip,
  length,
  complement
} from 'ramda'
import * as moment from 'moment-timezone'

export const isNot = (a: any) => (b: any) => complement(equals)(a, b)

moment()
  .tz('Europe/Helsinki')
  .format()

const checkTypes = process.env.NODE_ENV !== 'production'
export const S = create({ checkTypes, env: env.concat(flutureEnv) })

// safety utils
export const docDataOrNull = doc => (!doc || !doc.exists ? null : doc.data())
export const docIdOrNull = doc => (!doc || !doc.exists ? null : doc.id)
export const areValidResults = compose(
  not,
  either(isNil, has('error'))
)
export const notNil = compose(
  not,
  isNil
)

// date utils
export const addInsertionDate = assoc(
  'insertedOn',
  moment().format('YYYY-MM-DD HH:mm')
)
export const formatDate = date =>
  `${moment(date)
    .add(2, 'hours')
    .format('dddd, MMMM Do YYYY, HH:mm')}`
export const isWithin24Hours = datetime => {
  const start = moment(datetime)
  const end = moment(datetime).add(24, 'hours')
  return moment().isBetween(start, end)
}

// mail utils
export const createMailMsg = evolve({
  to: identity,
  from: v => (v ? v : 'richard.vancamp@gmail.com'),
  subject: s => `Web Dev & Sausages ${s || ''}`,
  text: identity
})
export const filterOutTokenAndEmail = (email: string, token: string) => queue =>
  queue.filter(reg => !(reg.email === email && reg.verificationToken === token))
export const findByEmailAndPassword = (
  email: string,
  verificationToken: string
) =>
  find(
    both(propEq('email', email), propEq('verificationToken', verificationToken))
  )

// other
export const findIndexOfRegistration = registration =>
  findIndex(equals(registration))
export const propHasLength = prop =>
  compose(
    flip(gt)(0),
    length,
    propOr([], prop)
  )
