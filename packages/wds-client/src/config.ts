let version = 'tool'

if (process.env.NODE_ENV === 'development') {
  // version = 'dev'
}

export const config = {
  API_ROOT: 'http://localhost:5000/api/1.0/',
  MAILING_LIST_URI: `https://us-central1-wds-event-${version}.cloudfunctions.net/api/`
}
