import config from '$config';

export default {
	currentEvent: `${config.API_ROOT}/api/1.0/events/current`,
	register: (id) => `${config.API_ROOT}/api/1.0/events/${id}/registrations`,
	checkRegistration: (id, token) => `${config.API_ROOT}/api/1.0/events/${id}/registrations/${token}`,
	cancelRegistration: (token) => `${config.API_ROOT}/api/1.0/events/registrations/${token}`
};
