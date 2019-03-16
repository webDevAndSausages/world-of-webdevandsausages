import { unionize, ofType, UnionOf } from 'unionize'

export interface FormState {
  verificationToken: string
  ready: boolean
  status?: number
  response?: any
}

export interface ModificationState extends FormState {
  error?: any
}

export const RegistrationModification = unionize(
  {
    Entering: ofType<ModificationState>(),
    EnteringValid: ofType<ModificationState>(),
    Success: ofType<ModificationState>(),
    Failure: ofType<ModificationState>(),
    Loading: ofType<ModificationState>(),
    Cancelled: ofType<ModificationState>()
  },
  { value: 'value' }
)

export type RegistrationModificationType = UnionOf<
  typeof RegistrationModification
>
