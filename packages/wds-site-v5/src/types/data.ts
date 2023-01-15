export type EventStatus = 'PLANNING' | 'VISIBLE' | 'OPEN' | 'OPEN_FULL' | 'OPEN_WITH_WAITLIST' | 'CLOSED' | 'CLOSED_WITH_FEEDBACK' | 'CANCELLED';

export type Event = {
  id: string
  contact: string | null;
  createdOn: string;
  date: string;
  details: string;
  location: string;
  maxParticipants: number;
  registrationOpens: string;
  status: EventStatus;
  updatedOn: string;
  volume: number;
}

export type Talk = {
    title: string;
    details: string;
    youtubeId: string;
    startTime: string;
    startSeconds: number;
    show: boolean;
}

export type Speaker = {
  name: string;
  talks: Talk[];
}

export type Sponsor = {
  name: string;
  href: string;
}

export type PageData = {
  speakers: Speaker[];
  currentEvent: Event | null;
  sponsors: {
    data: Sponsor[]
  };
}