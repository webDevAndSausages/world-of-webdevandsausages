import { Request, Response, NextFunction } from 'express'
import * as Joi from 'joi'
import * as moment from 'moment-timezone'
import Future, { tryP, reject, of } from 'fluture'
import * as createError from 'http-errors'
import {
  areValidResults,
  docDataOrNull,
  findByEmailAndPassword,
  findIndexOfRegistration,
  formatDate
} from '../utils'
import { sendMail } from '../services/mail'
import { participantSchema } from './schemas'
import { eventsRef } from '../services/db'
import {
  allPass,
  compose,
  prop,
  merge,
  identity,
  uniq,
  concat,
  find,
  propEq,
  or,
  propOr,
  inc
} from 'ramda'
import { upsertParticipant } from './participants'
const randomWord = require('random-word')
import { config } from '../'
const isDev = config.VERSION === 'dev'
const devPass = config.PASS

moment()
  .tz('Europe/Helsinki')
  .format()

const isNotClosed = compose(
  date => moment().isSameOrBefore(date),
  prop('registrationCloses')
)

const isOpen = compose(
  date => moment().isSameOrAfter(date),
  prop('registrationOpens')
)
const isEventOpen = allPass([isOpen, isNotClosed])

const getSuccessEmailSubstitutions = (details, action, verificationToken) => ({
  action,
  datetime: formatDate(details.datetime),
  location: details.location,
  token: verificationToken,
  sponsor: details.sponsor
})

const hasSpace = details => details.registered.length < details.maxParticipants

type registrationQueue = 'registered' | 'waitListed'

const pushToEventQueue = (
  email: string,
  type: registrationQueue,
  eventId: string,
  details: object
) => {
  // unique token allows cancellation of registration
  const verificationToken = isDev ? devPass : `${randomWord()}-${randomWord()}`
  const registration = {
    email,
    verificationToken
  }
  const updatedList = compose(uniq, concat(details[type]))([registration])

  return tryP(() =>
    eventsRef.doc(eventId).update({ [type]: updatedList })
  ).bimap(
    identity,
    merge({
      email: getSuccessEmailSubstitutions(
        details,
        type.toLowerCase(),
        verificationToken
      )
    })
  )
}

const findEmail = email => find(propEq('email', email))

const isPreviouslyRegistered = (details, email) =>
  or(
    compose(findEmail(email), propOr([], 'registered'))(details),
    compose(findEmail(email), propOr([], 'wailListed'))(details)
  )

const registerOrWaitlist = (eventId: string, email: string) => details => {
  const valid = areValidResults(details)

  if (isPreviouslyRegistered(details, email)) {
    return Future.reject(createError(400, 'This email is already registered'))
  }
  if (valid && isEventOpen(details) && hasSpace(details)) {
    return pushToEventQueue(email, 'registered', eventId, details)
  } else if (valid && isEventOpen(details)) {
    return pushToEventQueue(email, 'waitListed', eventId, details)
  }

  return Future.reject(createError(404, 'No open event found from database'))
}

export const register = (
  request: Request,
  response: Response,
  next: NextFunction
) => {
  const eventId = request.params.eventId
  const email = request.body.email

  return (
    Future((rej, res) => {
      const { error } = Joi.validate(request.body, participantSchema)
      return error ? rej(createError(422, error.message)) : res(null)
    })
      // save participant to db or merge with existing one
      .chain(upsertParticipant(request.body))
      // if event is open, add to registration list or wait list
      .chain(() => {
        return tryP(() => eventsRef.doc(eventId).get())
          .map(docDataOrNull)
          .chain(registerOrWaitlist(eventId, email))
      })
      .chain(result => {
        const msg = {
          to: email,
          from: 'richard.vancamp@gmail.com',
          subject: 'Web dev & sausages event registration',
          template_id: '1c9f75f0-babb-40d5-b953-d7373e2ffa23',
          substitutions: { ...result.email }
        }
        return sendMail(msg)
      })
      .fork(
        error => next(error),
        () => response.status(201).json({ success: true })
      )
  )
}

const findInRegistrationQueue = (
  eventId: string,
  email: string,
  token: string
) => details => {
  const valid = areValidResults(details)
  if (valid) {
    const isRegistered = compose(
      findByEmailAndPassword(email, token),
      prop('registered')
    )(details)
    const isWaitListed = compose(
      findByEmailAndPassword(email, token),
      prop('waitListed')
    )(details)

    if (!isRegistered && !isWaitListed) {
      return reject(
        createError(
          401,
          'Your email and verificationToken do not match a registration.'
        )
      )
    }

    if (isRegistered) {
      return of(merge(isRegistered, { waitListed: false }))
    }
    if (isWaitListed) {
      const place = compose(
        inc,
        findIndexOfRegistration(isWaitListed),
        prop('waitListed')
      )(details)
      return of(merge(isWaitListed, { waitListed: place }))
    }
    return reject(new createError.InternalServerError())
  }

  return reject(createError(404, 'No open event found from database'))
}

export const verifyRegistration = (
  request: Request,
  response: Response,
  next: NextFunction
) => {
  const eventId = request.params.eventId
  const email = request.query.e
  const token = request.query.t

  return Future((rej, res) => {
    const { error } = Joi.validate(
      { email, token },
      Joi.object({
        email: Joi.string()
          .email()
          .required(),
        token: Joi.string()
          .min(3)
          .required()
      })
    )
    return error ? rej(createError(422, error.message)) : res(null)
  })
    .chain(() => tryP(() => eventsRef.doc(eventId).get()))
    .map(docDataOrNull)
    .chain(findInRegistrationQueue(eventId, email, token))
    .fork(
      error => {
        console.log(error)
        next(error)
      },
      data => response.status(200).json({ data })
    )
}
