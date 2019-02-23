const Nexmo = require('nexmo')
import Future from 'fluture'
import { config } from '..'

const nexmo = new Nexmo({
  apiKey: config.NEXMO.KEY,
  apiSecret: config.NEXMO.SECRET
})

export const sendSms = (text: string, phoneNumber: string) => {
  return Future((rej, res) => {
    return nexmo.message.sendSms('WD&S', phoneNumber, text, (err, result) => {
      if (err) return rej(err)
      return res(result)
    })
  })
}
