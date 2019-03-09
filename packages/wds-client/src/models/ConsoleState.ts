import { unionize, ofType, UnionOf } from "unionize"

interface ConState {
  prompt?: string
  last: any
}

export const ConsoleState = unionize({
  Waiting: ofType<ConState>(),
  Registering: ofType<ConState>(),
  Modifing: ofType<ConState>(),
  Checking: ofType<ConState>(),
  Helping: ofType<ConState>()
})

export type ConsoleStateType = UnionOf<typeof ConsoleState>
