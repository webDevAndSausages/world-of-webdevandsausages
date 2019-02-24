export const machineConfig = {
  id: 'mailingList',
  context: {
    email: ''
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
            actions: 'send',
            target: 'awaitingResponse'
          }
        ]
      }
    },
    awaitingResponse: {
      invoke: {
        src: 'sendRequest',
        onDone: {
          target: 'success'
        },
        onError: [
          {
            cond: 'isIncorrectPassword',
            target: 'passwordErr.incorrect'
          },
          {
            cond: 'isServiceErr',
            target: 'serviceErr'
          }
        ]
      }
    },
    emailErr: {
      onEntry: 'focusEmailInput',
      on: {
        ENTER_EMAIL: {
          target: 'dataEntry',
          actions: 'cacheEmail'
        }
      },
      initial: 'badFormat',
      states: {
        badFormat: {}
      }
    },
    serviceErr: {
      onEntry: 'focusSubmitBtn',
      on: {
        SUBMIT: {
          target: 'awaitingResponse'
        }
      }
    },
    success: {
      type: 'final'
    }
  },
  onDone: {
    actions: 'reset'
  }
}

export default machineConfig
