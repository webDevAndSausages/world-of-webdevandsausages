import { always, ifElse } from 'ramda'

const emailRegex = /^.+@.+\..+$/i
const isEmail = (value) => emailRegex.test(value)
const createValidator = (cond, msg) => ifElse(cond, always(null), always(msg))
export const validateEmail = createValidator(isEmail, 'Invalid email')
