import { Unauthorized } from 'http-errors'

export const authorizeAdmin = (req, res, next) => {
  if (!req.session.user) {
    console.error('No session cookie was present.', req.session)
    next(new Unauthorized())
    return
  }
  next()
}
