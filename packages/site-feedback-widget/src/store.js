import { writable, derived } from 'svelte/store'
import storage from './localStorage'

const initialState = { rating: 0, text: '', current: 'idle', submitting: false, init: false }
// unlike redux with reduce on the current state
// and then possible actions within that state
// making it impossible to call wrong actconsion in wrong state
function reducer(event, state) {
  switch (state.current) {
    case 'idle':
      switch (event.type) {
        case 'SHOW':
          return { ...state, current: 'star-rating', init: true, event: event.type }
        default:
          return state
      }
    case 'star-rating':
      switch (event.type) {
        case 'RATE': {
          if (event.payload <= 3) {
            return { ...state, rating: event.payload, current: 'form', event: event.type }
          }
          return { ...state, rating: event.payload, current: 'thanks', event: event.type }
        }
        case 'CLOSE':
          return { ...initialState, event: event.type }
        default:
          return state
      }
    case 'form':
      switch (event.type) {
        case 'SUBMIT':
          return { ...state, text: event.payload, submitting: true, event: event.type }
        case 'FINISH':
          return { ...state, result: event.payload, current: 'thanks', event: event.type }
        case 'CLOSE':
          return { ...initialState, event: event.type }
        default:
          return state
      }
    case 'thanks':
      switch (event.type) {
        case 'CLOSE':
          return { ...initialState, event: event.type }
        default:
          return state
      }
    default:
      return state
  }
}

const actions = {
  show: (_, state) => reducer({ type: 'SHOW' }, state),
  rate: (payload, state) => reducer({ type: 'RATE', payload }, state),
  close: (_, state) => reducer({ type: 'CLOSE' }, state),
  submit: (payload, state) => reducer({ type: 'SUBMIT', payload }, state),
  finish: (payload, state) => reducer({ type: 'FINISH', payload }, state)
}

function reduce(actions, initial) {
  const state = writable(initial)

  const mappedActions = Object.keys(actions).reduce((acc, actionName) => {
    acc[actionName] = payload => state.update(state => actions[actionName](payload, state))
    return acc
  }, {})
  return { subscribe: state.subscribe, actions: mappedActions }
}

export const stateStore = reduce(actions, initialState)

export const hide = derived(stateStore, $a => {
  if ($a.current === 'rate' && $a.rating > 0) {
    storage.set('done')
  } else if ($a.init) {
    storage.set('seen')
  }
  const feedback = storage.get()
  return feedback === 'done' || feedback === 'seen'
})
