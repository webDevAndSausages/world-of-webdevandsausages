import { ApiRequest, RequestFromApi } from '../../models/ApiRequest'
import { MachineConfig } from 'xstate'

export interface MailingListSchema {
  states: {
    emailEntry: {}
    awaitingResponse: {}
    emailErr: {}
    serviceErr: {}
  }
}

// The events that the machine handles
export type MailingListEvent =
  | { type: 'ENTER_EMAIL' }
  | { type: 'EMAIL_BLUR' }
  | { type: 'SUBMIT' }
  | { type: 'SUCCESS' }
  | { type: 'FAILURE' }
  | { type: 'LOADING' }
  | { type: 'RESET' }

// The context (extended state) of the machine
export interface MailingListContext {
  email: string
  data: RequestFromApi
}

export type MailingListConfig = MachineConfig<
  MailingListContext,
  MailingListSchema,
  MailingListEvent
>

export const machineConfig: MailingListConfig = {
  id: 'mailingList',
  context: {
    email: '',
    data: ApiRequest.NOT_ASKED()
  },
  initial: 'emailEntry',
  states: {
    emailEntry: {
      on: {
        ENTER_EMAIL: {
          actions: 'cacheEmail'
        },
        EMAIL_BLUR: {
          cond: 'isBadEmailFormat',
          target: 'emailErr.badFormat'
        },
        SUBMIT: [
          {
            cond: 'isBadEmailFormat',
            target: 'emailErr.badFormat'
          },
          {
            target: 'awaitingResponse',
            actions: 'send'
          }
        ]
      }
    },
    awaitingResponse: {
      on: {
        SUCCESS: {
          actions: 'cacheRequest'
        },
        FAILURE: {
          actions: 'cacheRequest',
          target: 'serviceErr'
        },
        LOADING: {
          actions: 'cacheRequest'
        },
        RESET: {
          target: 'emailEntry',
          actions: 'reset'
        }
      }
    },
    emailErr: {
      on: {
        ENTER_EMAIL: {
          target: 'emailEntry',
          actions: 'cacheEmail'
        }
      },
      states: {
        badFormat: {}
      }
    },
    serviceErr: {
      on: {
        SUBMIT: {
          target: 'awaitingResponse'
        },
        ENTER_EMAIL: {
          target: 'emailEntry',
          actions: 'cacheEmail'
        }
      }
    }
  }
}

export default machineConfig
