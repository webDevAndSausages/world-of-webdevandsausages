const sgMail = require('@sendgrid/mail')
import { tryP } from 'fluture'
import { MailData } from '@sendgrid/helpers/classes/mail'
import { config } from '..'

export const sendMail = (msg: MailData) => {
  sgMail.setApiKey(config.SENDGRID_KEY)
  return tryP(() => sgMail.send(msg))
}
