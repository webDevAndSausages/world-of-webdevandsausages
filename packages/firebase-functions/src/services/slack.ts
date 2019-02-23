import axios from 'axios'
import { encaseP2 } from 'fluture'
import { config } from '../'
const axiosF = encaseP2(axios.post)

export const notifySlack = email => {
  const data = { text: `${email} joined the mailing list!` }
  axiosF(config.SLACK_URL, data).fork(
    err =>
      console.log('An error occurred dispatching a message to slack: ', err),
    () => console.log(`Slack has received a notification about ${email}`)
  )
}
