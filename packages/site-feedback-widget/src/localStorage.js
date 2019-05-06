const is = val => val && typeof val !== 'undefined'
const windowExists = is(window)
const KEY = '__$site-feedback-widget'

function getExpiration() {
  let dateObj = Date.now()
  // add 3 days
  dateObj += 1000 * 60 * 60 * 24 * 3
  return +new Date(dateObj)
}

export const storage = (store = window.localStorage) => ({
  get(defaultValue = null) {
    if (!windowExists) {
      return defaultValue
    }
    const value = store[KEY]
    const expiration = store[`${KEY}-expiration`]
    if ((is(value) && !is(expiration)) || value === 'done') {
      return value
    }
    if (is(value) && +new Date() < Number(expiration)) {
      return value
    }
    this.remove()
    return defaultValue
  },
  set(value, expirationMS = getExpiration()) {
    if (windowExists || hasWindow) {
      store[KEY] = value
      store[`${KEY}-expiration`] = expirationMS
    }
  },
  remove() {
    delete store[KEY]
    delete store[`${KEY}-expiration`]
  }
})

export default storage()
