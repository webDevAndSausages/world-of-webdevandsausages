import {writable, derived} from 'svelte/store'
import {isValidToken}  from '../utils'

export const createTokenStore = () => writable('')

export const createTokenValidationStore = tokenStore => derived(tokenStore, $t => {
  const error = isValidToken($t)
  return error
})
