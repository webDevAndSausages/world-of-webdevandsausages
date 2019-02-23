export default fns => ({
  VERSION: fns.config().app.version,
  PASS: fns.config().app.pass,
  SLACK_URL: fns.config().slack.url,
  SENDGRID_KEY: fns.config().sendgrid.key,
  GOOGLE: {
    SECRET: fns.config().googleapi.client_secret,
    ID: fns.config().googleapi.client_id,
    SHEET_ID: fns.config().googleapi.sheet_id
  },
  NEXMO: {
    SECRET: fns.config().nexmo.api_secret,
    KEY: fns.config().nexmo.api_key
  }
})
