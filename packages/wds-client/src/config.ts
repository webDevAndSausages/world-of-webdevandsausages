let version = 'tool'

if (process.env.NODE_ENV === 'development') {
  version = 'dev'
}

export const config = {
  API_ROOT: process.env.REACT_APP_API_ROOT,
  MAILING_LIST_URI: `https://us-central1-wds-event-${version}.cloudfunctions.net/api/`
}
