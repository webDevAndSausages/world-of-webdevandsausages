const cors = require('cors')
import * as cookieSession from 'firebase-cookie-session'
import { config } from '../'

const setMiddleware = app => {
  // credentials:true needed for cookies
  app.use(cors({ origin: true, credentials: true }))
  app.options('*', cors())
  app.use(
    // firebase fucntions only allow one cookie with the name __session
    cookieSession({
      secure: false,
      httpOnly: false,
      keys: [config.GOOGLE.SECRET],
      maxAge: 72 * 60 * 60 * 1000 // 72 hours
    })
  )
  return app
}

export default setMiddleware
