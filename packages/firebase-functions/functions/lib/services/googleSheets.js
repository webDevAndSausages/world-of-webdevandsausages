"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const { OAuth2Client } = require('google-auth-library');
const { google } = require('googleapis');
const fluture_1 = require("fluture");
const db_1 = require("./db");
const utils_1 = require("../utils");
const slack_1 = require("./slack");
const __1 = require("..");
const GOOGLE_AUTH_REDIRECT = `https://us-central1-wds-event-tool.cloudfunctions.net/oauthcallback`;
let oauthTokens = null;
const functionsOauthClient = new OAuth2Client(__1.config.GOOGLE.ID, __1.config.GOOGLE.SECRET, GOOGLE_AUTH_REDIRECT);
const SCOPES = ['https://www.googleapis.com/auth/spreadsheets'];
// visit the URL for this Function to obtain tokens
exports.authenticateForGoogleSheetsApi = (req, res) => {
    const redirectUrl = functionsOauthClient.generateAuthUrl({
        access_type: 'offline',
        scope: SCOPES,
        prompt: 'consent'
    });
    res.set('Cache-Control', 'private, max-age=0, s-maxage=0');
    res.redirect(redirectUrl);
};
exports.setGooogleSheetsApiTokens = (request, response) => {
    response.set('Cache-Control', 'private, max-age=0, s-maxage=0');
    const code = request.query.code;
    // TODO: how to encase this directly in a Future?
    return (fluture_1.tryP(() => functionsOauthClient.getToken(code))
        .chain(({ tokens }) => {
        console.log('tokens', tokens);
        return fluture_1.tryP(() => db_1.tokensRef.doc('googleSheetsApi').set(tokens));
    })
        .fork((err) => {
        console.log('Google OAuth token request failure: ', err);
        response.status(400).send(err);
    }, () => response.status(200).send('OK')));
};
function getAuthorizedClient() {
    return fluture_1.default((rej, res) => {
        if (oauthTokens) {
            return res(functionsOauthClient);
        }
        return res(null);
    }).chain((res) => {
        if (!res) {
            return fluture_1.tryP(() => db_1.tokensRef.doc('googleSheetsApi').get()).map(utils_1.docDataOrNull).chain((data) => {
                oauthTokens = data;
                functionsOauthClient.setCredentials(oauthTokens);
                return fluture_1.of(functionsOauthClient);
            });
        }
        return fluture_1.of(res);
    });
}
function appendFuture(requestWithoutAuth) {
    return getAuthorizedClient()
        .chain((client) => {
        console.log('google auth client: ', client);
        if (!client) {
            return fluture_1.reject(null);
        }
        const sheets = google.sheets('v4');
        const request = requestWithoutAuth;
        request.auth = client;
        return sheets.spreadsheets.values.append(request, (err, response) => {
            if (err) {
                console.log(`The API returned an error: ${err}`);
                return fluture_1.reject(err);
            }
            return fluture_1.of(response);
        });
    })
        .fork((err) => console.log('Appending email to spreadsheeet failed: ', err), () => console.log('New participant email saved to Google sheet.'));
}
const GOOGLE_SHEET_ID = __1.config.GOOGLE.SHEET_ID;
exports.appendNewEmailToSpreadsheetOnCreate = (snap) => {
    const newParticipant = snap.data();
    slack_1.notifySlack(newParticipant.email);
    console.log('Appending email to google sheet: ', newParticipant);
    return appendFuture({
        spreadsheetId: GOOGLE_SHEET_ID,
        range: 'A:C',
        valueInputOption: 'USER_ENTERED',
        insertDataOption: 'INSERT_ROWS',
        resource: {
            values: [[newParticipant.email, newParticipant.receivesMail, newParticipant.insertedOn]]
        }
    });
};
//# sourceMappingURL=googleSheets.js.map