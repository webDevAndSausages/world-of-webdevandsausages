import firebase from 'firebase/app'
import 'firebase/firestore'

const firebaseConfig = {
  apiKey: 'AIzaSyDiyZi8l5GhhUdl-Sr6vhLVVFz1LBFWl7Y',
  authDomain: 'wds-feedback.firebaseapp.com',
  databaseURL: 'https://wds-feedback.firebaseio.com',
  projectId: 'wds-feedback',
  storageBucket: 'wds-feedback.appspot.com',
  messagingSenderId: '18319457375',
  appId: '1:18319457375:web:81e870a0759d7d1e'
}

firebase.initializeApp(firebaseConfig)

const FEEDBACK = 'responses2019'

// data is saved in a yearly collection with
// document for each day of year
function getDayOfYear() {
  const now = new Date()
  const start = new Date(now.getFullYear(), 0, 0)
  const diff = now - start + (start.getTimezoneOffset() - now.getTimezoneOffset()) * 60 * 1000
  const oneDay = 1000 * 60 * 60 * 24
  const day = Math.floor(diff / oneDay)
  return `${day}`
}

function createDb() {
  const day = getDayOfYear()

  const collection = firebase.firestore().collection(FEEDBACK)

  const doc = collection.doc(day)

  const save = key => async value => {
    try {
      await doc.update({
        [key]: firebase.firestore.FieldValue.arrayUnion(value)
      })
    } catch (e) {
      // if doc doesn't exist create it with initial value
      await doc.set({ [key]: value })
    }
    return
  }

  return {
    saveRating: save('ratings'),
    saveSuggestion: save('suggestions')
  }
}

export const db = createDb()
