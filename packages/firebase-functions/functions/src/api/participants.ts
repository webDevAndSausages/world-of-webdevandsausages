import { Request, Response, NextFunction } from 'express'
import { JoiObject, validate } from 'joi'
import { participantSchema } from './schemas'
import { CollectionReference } from '@google-cloud/firestore'
import Future, { tryP, of, reject } from 'fluture'
import * as createError from 'http-errors'
import { docDataOrNull, docIdOrNull, addInsertionDate } from '../utils'
import { participantsRef } from '../services/db'
import {
  filter,
  traverse,
  mergeAll,
  isNil,
  isEmpty,
  either,
  complement
} from 'ramda'
import { IParticipant } from '../models'
import { sendMail } from '../services/mail'

export const safeData = (schema, withErrors, withId) => doc => {
  const data = docDataOrNull(doc)
  const id = docIdOrNull(doc)

  if (either(isNil, isEmpty)(data)) return of(null)
  const { error } = validate(data, schema)
  const dataWithExtras = [data]

  if (withErrors && error) {
    dataWithExtras.push({
      validationError: error.message ? error.message : error
    })
  }

  if (withId && id) {
    dataWithExtras.push({ id })
  }
  const result = mergeAll(dataWithExtras)
  return of(result)
}

export const getCollection = (
  dbRef: CollectionReference,
  schema: JoiObject
) => (req: Request, res: Response, next: NextFunction) => {
  return tryP(() => dbRef.get())
    .chain(docsSnapshots => {
      const docs = []
      docsSnapshots.forEach(d => docs.push(d))
      return traverse(of, safeData(schema, true, true), docs)
    })
    .map(filter(complement(isNil)))
    .fork(error => next(createError(500, error)), data => res.json({ data }))
}

export const getAllParticipants = getCollection(
  participantsRef,
  participantSchema
)

export const upsertParticipant = (body: IParticipant) => () => {
  const currentRef = participantsRef.doc(body.email)
  const participant = addInsertionDate(body)
  return tryP(() => currentRef.set(participant, { merge: true }))
}

export const addParticipant = (
  request: Request,
  response: Response,
  next: NextFunction
) => {
  return (
    Future((rej, res) => {
      const { error } = validate(request.body, participantSchema)
      return error ? rej(createError(422, error.message)) : res(null)
    })
      // save participant to db or merge with existing one
      .chain(upsertParticipant(request.body))
      .chain(() => tryP(() => participantsRef.doc(request.body.email).get()))
      .map(docDataOrNull)
      .chain(result => {
        const msg = {
          to: result.email,
          from: 'richard.vancamp@gmail.com',
          subject: "Wed dev & sausage's mailing list confirmation",
          template_id: '487d4c85-5cd0-4602-80e2-5120d2483f76'
        }
        return sendMail(msg)
      })
      .fork(
        error => {
          console.error('Failed to save participant to db: ', error)
          next(new createError.InternalServerError())
        },
        () => response.status(201).json({ success: true })
      )
  )
}

export const removeParticipant = (
  request: Request,
  response: Response,
  next: NextFunction
) => {
  const email = request.params.email
  const participant = participantsRef.doc(email)
  tryP(() => participant.get())
    .chain(doc => {
      if (doc && doc.exists) {
        return tryP(() => participant.delete())
      }
      return reject(new createError.NotFound())
    })
    .fork(
      error => next(error),
      () => response.status(202).json({ success: true })
    )
}
