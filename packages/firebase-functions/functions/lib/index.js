"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const functions = require("firebase-functions");
const firebase_admin_1 = require("firebase-admin");
const config_1 = require("./config");
exports.config = config_1.default(functions);
const serviceAccount = require(`../${exports.config.VERSION}ServiceAccountKey.json`);
firebase_admin_1.default.initializeApp({
    credential: firebase_admin_1.default.credential.cert(serviceAccount)
});
const express = require("express");
const apiRoutes_1 = require("./api/apiRoutes");
const _1 = require("./middleware/");
const googleSheets_1 = require("./services/googleSheets");
const app = express();
_1.default(app);
app.use(apiRoutes_1.default);
exports.api = functions.https.onRequest((req, res) => {
    // without trailing "/" will have req.path = null, req.url = null
    // which won't match to your app.get('/', ...) route
    if (!req.path)
        req.url = `/${req.url}`;
    return app(req, res);
});
/* NON-API ENDPOINTS */
exports.OauthCallback = functions.https.onRequest(googleSheets_1.setGooogleSheetsApiTokens);
exports.authGoogleAPI = functions.https.onRequest(googleSheets_1.authenticateForGoogleSheetsApi);
exports.appendNewEmailToSpreadSheet = functions.firestore
    .document(`participants/{participantId}`)
    .onCreate(googleSheets_1.appendNewEmailToSpreadsheetOnCreate);
//# sourceMappingURL=index.js.map