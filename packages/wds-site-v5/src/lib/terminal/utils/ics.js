import {timeZone} from '.'

/*
 * ICalendar specs & validatior:
 *   https://tools.ietf.org/
 *   https://icalendar.org/
 **/

const IMAGE_URI = 'http://bit.ly/31E542n'
// for 'https://pbs.twimg.com/profile_images/1183450866675863559/j6pBwE1y_400x400.jpg'
const CONTACT = 'richard.vancamp@gmail.com'

const sufficientData = ({volume, start, description}) =>
	volume && start && description

// TODO: Add validation
export function createIcs(data) {
	if (!sufficientData(data)) return undefined

	const SEPARATOR =
		!navigator || navigator.appVersion.indexOf('Win') !== -1 ? '\r\n' : '\n'

	const {volume, now, start, end, description, location = 'Tampere'} = data

	const subject = `Web Dev & Sausages Meetup Volume ${volume}`
	return [
		'BEGIN:VCALENDAR',
		'PRODID:Calendar',
		'VERSION:2.0',
		`X-WR-TIMEZONE:${timeZone}`,
		'BEGIN:VTIMEZONE',
		`TZID:${timeZone}`,
		'BEGIN:STANDARD',
		`DTSTART:${now}`,
		`TZNAME:${timeZone}`,
		'TZOFFSETFROM:+0300',
		'TZOFFSETTO:+0200',
		'END:STANDARD',
		'BEGIN:DAYLIGHT',
		`DTSTART:${now}`,
		`TZNAME:${timeZone}`,
		'TZOFFSETFROM:+0200',
		'TZOFFSETTO:+0300',
		'END:DAYLIGHT',
		'END:VTIMEZONE',
		'BEGIN:VEVENT',
		`UID:${volume}@webdevandsausages`,
		'CLASS:PUBLIC',
		`DESCRIPTION:${description}`,
		`X-ALT-DESC:${description}`,
		`DTSTAMP;TZID=${timeZone}:${now}`,
		`DTSTART;TZID=${timeZone}:${start}`,
		`DTEND;TZID=${timeZone}:${end}`,
		`LOCATION:${location}`,
		`SUMMARY;LANGUAGE=en-us:${subject}`,
		'URL:https://www.webdevandsausages.org/',
		`ORGANIZER;EMAIL=${CONTACT}:mailto:${CONTACT}`,
		'COLOR:deepskyblue',
		`IMAGE;VALUE=URI;DISPLAY=BADGE;FMTTYPE=image/png:${IMAGE_URI}`,
		'TRANSP:TRANSPARENT',
		'END:VEVENT',
		'END:VCALENDAR',
	].join(SEPARATOR)
}
