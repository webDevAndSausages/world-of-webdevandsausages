import * as functions from 'firebase-functions'
import admin from 'firebase-admin'
import getConfig from './config'
export const config = getConfig(functions)
const serviceAccount = require(`../${config.VERSION}ServiceAccountKey.json`)
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
})

import * as express from 'express'
import api from './api/apiRoutes'
import setMiddleware from './middleware/'
import {
  setGooogleSheetsApiTokens,
  authenticateForGoogleSheetsApi,
  appendNewEmailToSpreadsheetOnCreate
} from './services/googleSheets'

const app = express()
setMiddleware(app)
app.use(api)

exports.api = functions.https.onRequest(
  (req: express.Request, res: express.Response): express.Application => {
    // without trailing "/" will have req.path = null, req.url = null
    // which won't match to your app.get('/', ...) route
    if (!req.path) req.url = `/${req.url}`
    return app(req, res)
  }
)

/* NON-API ENDPOINTS */
exports.OauthCallback = functions.https.onRequest(setGooogleSheetsApiTokens)
exports.authGoogleAPI = functions.https.onRequest(
  authenticateForGoogleSheetsApi
)
exports.appendNewEmailToSpreadSheet = functions.firestore
  .document(`participants/{participantId}`)
  .onCreate(appendNewEmailToSpreadsheetOnCreate)
