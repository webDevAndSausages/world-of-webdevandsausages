import { unionize, ofType, UnionOf } from 'unionize'

export interface FormState {
  email: string
  firstName: string
  lastName: string
  affiliation: string
  ready: boolean
  status?: number
  response?: any
}

export interface RegistrationState extends FormState {
  error?: any
}

export const Registration = unionize(
  {
    Entering: ofType<RegistrationState>(),
    EnteringValid: ofType<RegistrationState>(),
    Success: ofType<RegistrationState>(),
    Failure: ofType<RegistrationState>(),
    Loading: ofType<RegistrationState>(),
    Cancelled: ofType<RegistrationState>()
  },
  { value: 'value' }
)

export type RegistrationType = UnionOf<typeof Registration>
