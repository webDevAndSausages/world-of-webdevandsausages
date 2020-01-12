"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Joi = require("joi");
const moment = require("moment-timezone/moment-timezone");
const fluture_1 = require("fluture");
const createError = require("http-errors");
const utils_1 = require("../utils");
const mail_1 = require("../services/mail");
const schemas_1 = require("./schemas");
const db_1 = require("../services/db");
const ramda_1 = require("ramda");
const participants_1 = require("./participants");
const randomWord = require('random-word');
const _1 = require("../");
const isDev = _1.config.VERSION === 'dev';
const devPass = _1.config.PASS;
moment().tz('Europe/Helsinki').format();
const isNotClosed = ramda_1.compose((date) => moment().isSameOrBefore(date), ramda_1.prop('registrationCloses'));
const isOpen = ramda_1.compose((date) => moment().isSameOrAfter(date), ramda_1.prop('registrationOpens'));
const isEventOpen = ramda_1.allPass([isOpen, isNotClosed]);
const getSuccessEmailSubstitutions = (details, action, verificationToken) => ({
    action,
    datetime: utils_1.formatDate(details.datetime),
    location: details.location,
    token: verificationToken,
    sponsor: details.sponsor
});
const hasSpace = (details) => details.registered.length < details.maxParticipants;
const pushToEventQueue = (email, type, eventId, details) => {
    // unique token allows cancellation of registration
    const verificationToken = isDev ? devPass : `${randomWord()}-${randomWord()}`;
    const registration = {
        email,
        verificationToken
    };
    const updatedList = ramda_1.compose(ramda_1.uniq, ramda_1.concat(details[type]))([registration]);
    return fluture_1.tryP(() => db_1.eventsRef.doc(eventId).update({ [type]: updatedList })).bimap(ramda_1.identity, ramda_1.merge({
        email: getSuccessEmailSubstitutions(details, type.toLowerCase(), verificationToken)
    }));
};
const findEmail = (email) => ramda_1.find(ramda_1.propEq('email', email));
const isPreviouslyRegistered = (details, email) => ramda_1.or(ramda_1.compose(findEmail(email), ramda_1.propOr([], 'registered'))(details), ramda_1.compose(findEmail(email), ramda_1.propOr([], 'wailListed'))(details));
const registerOrWaitlist = (eventId, email) => (details) => {
    const valid = utils_1.areValidResults(details);
    if (isPreviouslyRegistered(details, email)) {
        return fluture_1.default.reject(createError(400, 'This email is already registered'));
    }
    if (valid && isEventOpen(details) && hasSpace(details)) {
        return pushToEventQueue(email, 'registered', eventId, details);
    }
    else if (valid && isEventOpen(details)) {
        return pushToEventQueue(email, 'waitListed', eventId, details);
    }
    return fluture_1.default.reject(createError(404, 'No open event found from database'));
};
exports.register = (request, response, next) => {
    const eventId = request.params.eventId;
    const email = request.body.email;
    return (fluture_1.default((rej, res) => {
        const { error } = Joi.validate(request.body, schemas_1.participantSchema);
        return error ? rej(createError(422, error.message)) : res(null);
    })
        .chain(participants_1.upsertParticipant(request.body))
        .chain(() => {
        return fluture_1.tryP(() => db_1.eventsRef.doc(eventId).get())
            .map(utils_1.docDataOrNull)
            .chain(registerOrWaitlist(eventId, email));
    })
        .chain((result) => {
        const msg = {
            to: email,
            from: 'richard.vancamp@gmail.com',
            subject: 'Web dev & sausages event registration',
            template_id: '1c9f75f0-babb-40d5-b953-d7373e2ffa23',
            substitutions: Object.assign({}, result.email)
        };
        return mail_1.sendMail(msg);
    })
        .fork((error) => next(error), () => response.status(201).json({ success: true })));
};
const findInRegistrationQueue = (eventId, email, token) => (details) => {
    const valid = utils_1.areValidResults(details);
    if (valid) {
        const isRegistered = ramda_1.compose(utils_1.findByEmailAndPassword(email, token), ramda_1.prop('registered'))(details);
        const isWaitListed = ramda_1.compose(utils_1.findByEmailAndPassword(email, token), ramda_1.prop('waitListed'))(details);
        if (!isRegistered && !isWaitListed) {
            return fluture_1.reject(createError(401, 'Your email and verificationToken do not match a registration.'));
        }
        if (isRegistered) {
            return fluture_1.of(ramda_1.merge(isRegistered, { waitListed: false }));
        }
        if (isWaitListed) {
            const place = ramda_1.compose(ramda_1.inc, utils_1.findIndexOfRegistration(isWaitListed), ramda_1.prop('waitListed'))(details);
            return fluture_1.of(ramda_1.merge(isWaitListed, { waitListed: place }));
        }
        return fluture_1.reject(new createError.InternalServerError());
    }
    return fluture_1.reject(createError(404, 'No open event found from database'));
};
exports.verifyRegistration = (request, response, next) => {
    const eventId = request.params.eventId;
    const email = request.query.e;
    const token = request.query.t;
    return fluture_1.default((rej, res) => {
        const { error } = Joi.validate({ email, token }, Joi.object({
            email: Joi.string().email().required(),
            token: Joi.string().min(3).required()
        }));
        return error ? rej(createError(422, error.message)) : res(null);
    })
        .chain(() => fluture_1.tryP(() => db_1.eventsRef.doc(eventId).get()))
        .map(utils_1.docDataOrNull)
        .chain(findInRegistrationQueue(eventId, email, token))
        .fork((error) => {
        console.log(error);
        next(error);
    }, (data) => response.status(200).json({ data }));
};
//# sourceMappingURL=register.js.map