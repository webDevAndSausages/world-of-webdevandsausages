import { unionize, ofType, UnionOf } from "unionize"

interface Prompt {
  prompt: string
  last?: any
}

export const Registration = unionize(
  {
    EnteringEmail: ofType<Prompt>(),
    EnteringName: ofType<Prompt>(),
    EnteringAffiliation: ofType<Prompt>(),
    Success: ofType<Prompt>(),
    Failure: ofType<Prompt>(),
    Loading: {}
  },
  {
    value: "value"
  }
)

export type RegistrationType = UnionOf<typeof Registration>
