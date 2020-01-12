import { Errback, Request, Response } from 'express'
import { pick } from 'ramda'
import { NextFunction } from 'express-serve-static-core'

const parseError = pick(['status', 'message'])

export const apiErrorHandler = (
  err: Errback,
  req: Request,
  res: Response,
  next: NextFunction
) => {
  console.log('Error', err.toString())
  const { status, message } = parseError(err)
  res.status(status || 500).json({ error: message || 'Oops, a goof happened.' })
}
