export interface IEventRegistration {
  validationToken: string
  email: string
}

export interface IParticipant {
  firstName?: string
  lastName?: string
  affiliation?: boolean
  email: string
  insertedOn: string
}

export interface IMailMsg {
  to: string
  from?: string
  subject?: string
  text?: string
}

export interface IEvent {
  datetime: string
  details: string
  location?: string
  maxParticipants: number
  registered?: any[] | number
  registrationOpens?: string
  registrationCloses?: string
  sponsor?: string
  sponsorWWWLink: string
  waitingListed?: any[] | number
}
