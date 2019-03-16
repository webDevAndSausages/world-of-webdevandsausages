import { unionize, ofType, UnionOf } from 'unionize'

export interface FormState {
  verificationToken: string
  ready: boolean
  status?: number
  response?: any
}

export interface CheckState extends FormState {
  error?: any
}

export const RegistrationCheck = unionize(
  {
    Entering: ofType<CheckState>(),
    EnteringValid: ofType<CheckState>(),
    Success: ofType<CheckState>(),
    Failure: ofType<CheckState>(),
    Loading: ofType<CheckState>(),
    Cancelled: ofType<CheckState>()
  },
  { value: 'value' }
)

export type RegistrationCheckType = UnionOf<typeof RegistrationCheck>
