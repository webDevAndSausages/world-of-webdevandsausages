const { OAuth2Client } = require('google-auth-library')
const { google } = require('googleapis')
import Future, { tryP, of, reject } from 'fluture'
import { tokensRef } from './db'
import { docDataOrNull } from '../utils'
import { notifySlack } from './slack'
import { config } from '..'

const GOOGLE_AUTH_REDIRECT = `https://us-central1-wds-event-${
  config.VERSION === 'dev' ? 'dev' : 'tool'
}.cloudfunctions.net/OauthCallback`

let oauthTokens = null

const functionsOauthClient = new OAuth2Client(
  config.GOOGLE.ID,
  config.GOOGLE.SECRET,
  GOOGLE_AUTH_REDIRECT
)

const SCOPES = ['https://www.googleapis.com/auth/spreadsheets']

// visit the URL for this Function to obtain tokens
export const authenticateForGoogleSheetsApi = (req, res) => {
  const redirectUrl = functionsOauthClient.generateAuthUrl({
    access_type: 'offline',
    scope: SCOPES,
    prompt: 'consent'
  })
  res.set('Cache-Control', 'private, max-age=0, s-maxage=0')
  res.redirect(redirectUrl)
}

export const setGooogleSheetsApiTokens = (request, response) => {
  response.set('Cache-Control', 'private, max-age=0, s-maxage=0')
  const code = request.query.code
  // TODO: how to encase this directly in a Future?
  functionsOauthClient.getToken(code, (error, token) => {
    Future((rej, res) => (error ? rej(error) : res(token)))
      // Now tokens contains an access_token and an optional refresh_token. Save them.
      .chain(tokens => tryP(() => tokensRef.doc('googleSheetsApi').set(tokens)))
      .fork(
        err => {
          console.log('Google OAuth token request failure: ', err)
          response.status(400).send(err)
        },
        () => response.status(200).send('OK')
      )
  })
}

function getAuthorizedClient() {
  return Future((rej, res) => {
    if (oauthTokens) {
      return res(functionsOauthClient)
    }
    return res(null)
  }).chain(res => {
    if (!res) {
      return tryP(() => tokensRef.doc('googleSheetsApi').get())
        .map(docDataOrNull)
        .chain(data => {
          oauthTokens = data
          functionsOauthClient.setCredentials(oauthTokens)
          return of(functionsOauthClient)
        })
    }
    return of(res)
  })
}

function appendFuture(requestWithoutAuth) {
  return getAuthorizedClient()
    .chain(client => {
      if (!client) {
        return reject(null)
      }
      const sheets = google.sheets('v4')
      const request = requestWithoutAuth
      request.auth = client

      return sheets.spreadsheets.values.append(request, (err, response) => {
        if (err) {
          console.log(`The API returned an error: ${err}`)
          return reject(err)
        }

        return of(response)
      })
    })
    .fork(
      err => console.log('Appending email to spreadsheeet failed: ', err),
      () => console.log('New participant email saved to Google sheet.')
    )
}

const GOOGLE_SHEET_ID = config.GOOGLE.SHEET_ID

export const appendNewEmailToSpreadsheetOnCreate = (snap, _context) => {
  const newParticipant = snap.data()
  notifySlack(newParticipant.email)

  return appendFuture({
    spreadsheetId: GOOGLE_SHEET_ID,
    range: 'A:C',
    valueInputOption: 'USER_ENTERED',
    insertDataOption: 'INSERT_ROWS',
    resource: {
      values: [
        [
          newParticipant.email,
          newParticipant.receivesMail,
          newParticipant.insertedOn
        ]
      ]
    }
  })
}
