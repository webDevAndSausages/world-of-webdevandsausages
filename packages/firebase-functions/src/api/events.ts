import { Request, Response, NextFunction } from 'express'
import { getCollection, safeData } from './participants'
import { eventsRef } from '../services/db'
import { eventSchema } from './schemas'
import { validate } from 'joi'
import Future, { tryP, of, reject } from 'fluture'
import { NotFound, InternalServerError, UnprocessableEntity } from 'http-errors'
import {
  filter,
  traverse,
  compose,
  prop,
  evolve,
  length,
  always,
  ifElse,
  has
} from 'ramda'
import * as moment from 'moment-timezone'
import { IEvent } from '../models'
import { docDataOrNull, isWithin24Hours } from '../utils'

export const getAllEvents = getCollection(eventsRef, eventSchema)

const isFutureEvent = compose(
  date => moment().isSameOrBefore(moment(date).add(26, 'hours')),
  prop('datetime')
)

const transformations = {
  registered: length,
  waitListed: length,
  feedback: null,
  feedbackPass: null
}
const safeHead = array => (array.length > 0 ? array[0] : {})
const transformForPublicApi = (current: IEvent[]): any => {
  const currentEvent = compose(evolve(transformations), safeHead)(current)
  const feedbackOpen = compose(
    ifElse(
      has('datetime'),
      compose(isWithin24Hours, prop('datetime')),
      always(false)
    )
  )(currentEvent)
  return { currentEvent, feedbackOpen }
}

export const getCurrentEvent = (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  return tryP(() => eventsRef.get())
    .chain(docsSnapshots => {
      const docs = []
      docsSnapshots.forEach(d => docs.push(d))
      return traverse(of, safeData(eventSchema, false, true), docs)
    })
    .chain(events => {
      const current = filter(isFutureEvent, events)
      return of(transformForPublicApi(current as IEvent[]))
    })
    .fork(error => next(error), data => res.json({ data }))
}

export const createEvent = (
  request: Request,
  respose: Response,
  next: NextFunction
) => {
  const newEvent = request.body
  Future((rej, res) => {
    const { error, value } = validate(newEvent, eventSchema)
    return error ? rej(new UnprocessableEntity(error.message)) : res(value)
  })
    .chain(event => {
      return tryP(() => eventsRef.add(event)).chain(docRef => {
        if (docRef.id) {
          return tryP(() => eventsRef.doc(docRef.id).get()).map(docDataOrNull)
        }
        return reject(new InternalServerError())
      })
    })
    .fork(error => next(error), data => respose.json({ data }))
}

export const updateEvent = (
  request: Request,
  respose: Response,
  next: NextFunction
) => {
  const eventId = request.params.id
  const updates = request.body
  Future((rej, res) => {
    const { error } = validate(updates, eventSchema, { noDefaults: true })
    return error ? rej(new UnprocessableEntity(error.message)) : res(null)
  })
    .chain(() => {
      return tryP(() => eventsRef.doc(eventId).update(updates)).chain(() => {
        return tryP(() => eventsRef.doc(eventId).get()).map(docDataOrNull)
      })
    })
    .fork(error => next(error), data => respose.json({ data }))
}

export const removeEvent = (
  request: Request,
  response: Response,
  next: NextFunction
) => {
  const eventId = request.params.id
  const event = eventsRef.doc(eventId)
  tryP(() => event.get())
    .chain(doc => {
      if (doc && doc.exists) {
        return tryP(() => event.delete())
      }
      return reject(new NotFound())
    })
    .fork(
      error => next(error),
      () => response.status(202).json({ success: true })
    )
}
