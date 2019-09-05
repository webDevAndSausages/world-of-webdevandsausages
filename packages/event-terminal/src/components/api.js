import config from './config'

export default {
	currentEvent: `${config.API_ROOT}events/current`,
	register: id => `${config.API_ROOT}events/${id}/registrations`,
	checkRegistration: (id, token) =>
		`${config.API_ROOT}events/${id}/registrations/${token}`,
	cancelRegistration: token =>
		`${config.API_ROOT}events/registrations/${token}`,
}
