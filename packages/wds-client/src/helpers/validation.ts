const emailRegex = /^.+@.+\..+$/i
export const isEmail = (value: string) => emailRegex.test(value)
