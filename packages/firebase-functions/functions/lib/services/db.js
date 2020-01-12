"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const firebase_admin_1 = require("firebase-admin");
const db = firebase_admin_1.default.firestore();
exports.participantsRef = db.collection('participants');
exports.eventsRef = db.collection('events');
exports.tokensRef = db.collection('api_tokens');
exports.adminsRef = db.collection('admins');
//# sourceMappingURL=db.js.map