import admin from 'firebase-admin'

const db = admin.firestore()
export const participantsRef = db.collection('participants')
export const eventsRef = db.collection('events')
export const tokensRef = db.collection('api_tokens')
export const adminsRef = db.collection('admins')
