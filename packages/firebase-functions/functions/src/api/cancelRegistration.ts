import * as Joi from 'joi'
import Future, { tryP, reject, of } from 'fluture'
import { eventsRef } from '../services/db'
import * as createError from 'http-errors'
import {
  docDataOrNull,
  areValidResults,
  filterOutTokenAndEmail,
  propHasLength,
  formatDate
} from '../utils'
import { evolve, identity, merge, pick, compose } from 'ramda'
import { sendMail } from '../services/mail'

const getSuccessEmailSubstitutions = compose(
  evolve({ datetime: formatDate }),
  pick(['location', 'datetime', 'sponsor'])
)

const getUpdateEmailSubstitutions = (details, token) => ({
  ...getSuccessEmailSubstitutions(details),
  token
})

const removeFromRegistrationQueue = (
  eventId: string,
  email: string,
  token: string
) => details => {
  const valid = areValidResults(details)
  if (valid) {
    const updatedEvent = compose(
      evolve({
        waitListed: filterOutTokenAndEmail(email, token),
        registered: filterOutTokenAndEmail(email, token)
      }),
      pick(['waitListed', 'registered'])
    )(details)

    if (
      updatedEvent.waitListed.length === details.waitListed.length &&
      updatedEvent.registered.length === details.registered.length
    ) {
      return reject(
        createError(
          401,
          'Your email and verificationToken do not match a registration.'
        )
      )
    }

    return tryP(() => eventsRef.doc(eventId).update(updatedEvent)).bimap(
      identity,
      merge({ email: getSuccessEmailSubstitutions(details) })
    )
  }

  return reject(createError(404, 'No open event found from database'))
}

const shiftFromWaitingList = eventId =>
  tryP(() => eventsRef.doc(eventId).get())
    .map(docDataOrNull)
    .chain(details => {
      if (
        details &&
        propHasLength('waitListed')(details) &&
        details.registered.length < details.maxParticipants
      ) {
        const shiftedParticipant = details.waitListed.pop()
        details.registered.push(shiftedParticipant)

        return tryP(() => eventsRef.doc(eventId).update(details)).chain(
          result => {
            const msg = {
              to: shiftedParticipant.email,
              from: 'richard.vancamp@gmail.com',
              subject: 'Web dev & sausages registration update',
              template_id: '7caff2cb-e29c-4a21-8717-32683bc5c1df',
              substitutions: {
                ...getUpdateEmailSubstitutions(
                  details,
                  shiftedParticipant.verificationToken
                )
              }
            }
            return sendMail(msg)
          }
        )
      }
      return of(null)
    })

export const cancelRegistration = (request, response, next) => {
  const eventId = request.params.eventId
  const email = request.body.email
  const token = request.body.verificationToken

  return (
    Future((rej, res) => {
      const { error } = Joi.validate(
        request.body,
        Joi.object({
          email: Joi.string()
            .email()
            .required(),
          verificationToken: Joi.string()
            .min(3)
            .required()
        })
      )
      return error ? rej(createError(422, error.message)) : res(null)
    })
      .chain(() => tryP(() => eventsRef.doc(eventId).get()))
      .map(docDataOrNull)
      .chain(removeFromRegistrationQueue(eventId, email, token))
      .chain(result => {
        const msg = {
          to: email,
          from: 'richard.vancamp@gmail.com',
          subject: 'Web dev & sausages registration cancellation',
          template_id: '5dd324f9-eb32-459a-b3ab-2177b0e6a6a5',
          substitutions: { ...result.email }
        }
        return sendMail(msg)
      })
      // return value of first future in chain so cancellation should work even if shiftWaitingList fails
      .chain(val => shiftFromWaitingList(eventId).chain(() => of(val)))
      .fork(
        error => next(error),
        () => response.status(202).json({ success: true })
      )
  )
}
