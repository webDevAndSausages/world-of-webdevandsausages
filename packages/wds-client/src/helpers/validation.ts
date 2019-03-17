const emailRegex = /^.+@.+\..+$/i
export const isEmail = (value: string) => emailRegex.test(value)
export const tokenRegex = /^[a-z]+-[a-z]+$/i
export const isToken = (value: string) => tokenRegex.test(value)
