import { useEffect, useRef, useMemo, useState } from 'react'
import { Machine } from 'xstate'
import { BehaviorSubject } from 'rxjs'
import { filter } from 'rxjs/operators'

export const onEvent = (...keys) => source =>
  source.pipe(
    filter(([state]) => {
      const { event } = state
      if (!event || !event.type) return false
      const len = keys.length
      if (len === 1) {
        return event.type === keys[0]
      } else {
        for (let i = 0; i < len; i++) {
          if (event.type === keys[i]) {
            return true
          }
        }
      }
      return false
    })
  )

export const onState = (...keys) => source =>
  source.pipe(
    filter(([state]) => {
      const { value } = state
      if (!value) return false
      const len = keys.length
      if (len === 1) {
        return value === keys[0]
      } else {
        for (let i = 0; i < len; i++) {
          if (value === keys[i]) {
            return true
          }
        }
      }
      return false
    })
  )

export function useRxMachine<Config, Options>(config, options, epics = []) {
  const machine = useMemo(() => Machine(config, options), [])
  let inputsRef$ = useRef(null)
  let sendRef = useRef(null)

  const [state, setState] = useState(machine.initialState)

  useEffect(() => {
    const machineState$ = new BehaviorSubject([
      machine.initialState,
      sendRef.current
    ])

    inputsRef$.current = machineState$

    const effects$ = epics.map(epic => epic(inputsRef$.current))

    const effectSubscriptions$ = effects$.map(fx => fx.subscribe())

    return () => effectSubscriptions$.forEach(fx => fx.unsubscribe())
  }, [sendRef])

  // Setup the service only once.
  const service = useMemo(() => {
    // send takes event type and optional value to update context with
    function send({ type, value }: { type: string; value?: any }) {
      let nextState = machine.transition(state, type)
      const { actions, context: ctx } = nextState

      const nextContext = actions.reduce((ctx, action) => {
        if (action.exec) {
          // context updated left -> right
          const result = action.exec(ctx, value, undefined)
          return { ...ctx, ...result }
        }
        return ctx
      }, ctx)

      nextState.context = nextContext
      setState(nextState)

      inputsRef$.current.next([nextState, sendRef.current])
    }

    function update(value) {
      const updatedCtx = { ...machine.context, ...value }
      state.context = updatedCtx
      setState(state)
    }

    sendRef.current = send

    return { send, update }
  }, [state.context])

  return {
    state,
    send: service.send,
    context: state.context,
    update: service.update
  }
}
