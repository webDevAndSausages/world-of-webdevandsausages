"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Joi = require("joi");
const fluture_1 = require("fluture");
const db_1 = require("../services/db");
const createError = require("http-errors");
const utils_1 = require("../utils");
const ramda_1 = require("ramda");
const mail_1 = require("../services/mail");
const getSuccessEmailSubstitutions = ramda_1.compose(ramda_1.evolve({ datetime: utils_1.formatDate }), ramda_1.pick(['location', 'datetime', 'sponsor']));
const getUpdateEmailSubstitutions = (details, token) => (Object.assign({}, getSuccessEmailSubstitutions(details), { token }));
const removeFromRegistrationQueue = (eventId, email, token) => details => {
    const valid = utils_1.areValidResults(details);
    if (valid) {
        const updatedEvent = ramda_1.compose(ramda_1.evolve({
            waitListed: utils_1.filterOutTokenAndEmail(email, token),
            registered: utils_1.filterOutTokenAndEmail(email, token)
        }), ramda_1.pick(['waitListed', 'registered']))(details);
        if (updatedEvent.waitListed.length === details.waitListed.length &&
            updatedEvent.registered.length === details.registered.length) {
            return fluture_1.reject(createError(401, 'Your email and verificationToken do not match a registration.'));
        }
        return fluture_1.tryP(() => db_1.eventsRef.doc(eventId).update(updatedEvent)).bimap(ramda_1.identity, ramda_1.merge({ email: getSuccessEmailSubstitutions(details) }));
    }
    return fluture_1.reject(createError(404, 'No open event found from database'));
};
const shiftFromWaitingList = eventId => fluture_1.tryP(() => db_1.eventsRef.doc(eventId).get())
    .map(utils_1.docDataOrNull)
    .chain(details => {
    if (details &&
        utils_1.propHasLength('waitListed')(details) &&
        details.registered.length < details.maxParticipants) {
        const shiftedParticipant = details.waitListed.pop();
        details.registered.push(shiftedParticipant);
        return fluture_1.tryP(() => db_1.eventsRef.doc(eventId).update(details)).chain(result => {
            const msg = {
                to: shiftedParticipant.email,
                from: 'richard.vancamp@gmail.com',
                subject: 'Web dev & sausages registration update',
                template_id: '7caff2cb-e29c-4a21-8717-32683bc5c1df',
                substitutions: Object.assign({}, getUpdateEmailSubstitutions(details, shiftedParticipant.verificationToken))
            };
            return mail_1.sendMail(msg);
        });
    }
    return fluture_1.of(null);
});
exports.cancelRegistration = (request, response, next) => {
    const eventId = request.params.eventId;
    const email = request.body.email;
    const token = request.body.verificationToken;
    return (fluture_1.default((rej, res) => {
        const { error } = Joi.validate(request.body, Joi.object({
            email: Joi.string()
                .email()
                .required(),
            verificationToken: Joi.string()
                .min(3)
                .required()
        }));
        return error ? rej(createError(422, error.message)) : res(null);
    })
        .chain(() => fluture_1.tryP(() => db_1.eventsRef.doc(eventId).get()))
        .map(utils_1.docDataOrNull)
        .chain(removeFromRegistrationQueue(eventId, email, token))
        .chain(result => {
        const msg = {
            to: email,
            from: 'richard.vancamp@gmail.com',
            subject: 'Web dev & sausages registration cancellation',
            template_id: '5dd324f9-eb32-459a-b3ab-2177b0e6a6a5',
            substitutions: Object.assign({}, result.email)
        };
        return mail_1.sendMail(msg);
    })
        .chain(val => shiftFromWaitingList(eventId).chain(() => fluture_1.of(val)))
        .fork(error => next(error), () => response.status(202).json({ success: true })));
};
//# sourceMappingURL=cancelRegistration.js.map