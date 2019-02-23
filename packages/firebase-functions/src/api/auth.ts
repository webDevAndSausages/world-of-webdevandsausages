const randomWord = require('random-word')
import { sendSms } from '../services/sms'
import { sendMail } from '../services/mail'
import { adminsRef } from '../services/db'
import { tryP, reject, both, of } from 'fluture'
import { docDataOrNull } from '../utils'
import { Unauthorized, InternalServerError } from 'http-errors'
import { compose, toLower, pathOr } from 'ramda'
import * as moment from 'moment'

const sendCodeByEmail = (data, pass, name) => {
  const msg = {
    to: data.email,
    from: 'richard.vancamp@gmail.com',
    subject: 'Web dev & sausages admin',
    template_id: 'f382f085-52eb-49ba-b7c6-ba58b9b61e97',
    substitutions: {
      token: pass,
      user: name
    }
  }
  return sendMail(msg)
}

const sendCodeBySms = (data, pass) => sendSms(pass, data.phoneNumber)

export const getCodeByEmailOrSms = (req, res, next) => {
  const name = compose(toLower, pathOr('', ['params', 'id']))(req)
  const bySms = req.query.method && req.query.method === 'sms'
  const pass = randomWord()
  const expires = moment()
    .add(10, 'minutes')
    .unix()
  const sender = bySms ? sendCodeBySms : sendCodeByEmail

  return tryP(() => adminsRef.doc(name).get())
    .map(docDataOrNull)
    .chain(data => {
      if (data && data.phoneNumber) {
        return both(
          tryP(() => adminsRef.doc(name).update({ pass, expires })),
          sender(data, pass, name)
        )
      }
      return reject(new Unauthorized())
    })
    .fork(
      err => {
        console.log(err)
        next(new InternalServerError())
      },
      () => res.status(200).json({ status: 'success' })
    )
}

const isValidPass = (data, pass) => {
  const unixNow = moment().unix()
  return (
    data.pass && data.expires && data.pass === pass && unixNow <= data.expires
  )
}

export const auth = (req, res, next) => {
  const name = req.session.user
  console.log(req.session)
  tryP(() => adminsRef.doc(name).get())
    .map(docDataOrNull)
    .chain(data => {
      if (!data) {
        req.session = null
        return reject(new Unauthorized())
      }
      return of({ name, admin: true })
    })
    .fork(err => next(err), data => res.status(200).send({ data }))
}

export const login = (req, res, next) => {
  const pass = req.body.pass
  const name = req.body.name
  tryP(() => adminsRef.doc(name).get())
    .map(docDataOrNull)
    .chain(data => {
      if (isValidPass(data, pass)) {
        req.session.user = name
        return of({ name, admin: true })
      }
      return reject(new Unauthorized())
    })
    .fork(err => next(err), data => res.status(200).send({ data }))
}

export const logout = (req, res, next) => {
  req.session = null
  res.status(200).json({ user: null })
}
