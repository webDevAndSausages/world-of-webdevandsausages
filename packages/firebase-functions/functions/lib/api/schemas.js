"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Joi = require("joi");
exports.participantSchema = Joi.object().keys({
    firstName: Joi.string(),
    lastName: Joi.string(),
    email: Joi.string()
        .email()
        .required(),
    receivesMail: Joi.boolean().required(),
    affiliation: Joi.string(),
    insertedOn: Joi.date()
});
const registrationSchema = Joi.object().keys({
    email: Joi.string()
        .email()
        .required(),
    verificationToken: Joi.string().required()
});
exports.eventSchema = Joi.object().keys({
    id: Joi.string(),
    contact: Joi.string().default(''),
    datetime: Joi.date().required(),
    details: Joi.string().default(''),
    location: Joi.string()
        .min(3)
        .required(),
    maxParticipants: Joi.number()
        .integer()
        .required(),
    registered: Joi.array()
        .unique()
        .items(registrationSchema)
        .default([]),
    registrationOpens: Joi.date(),
    registrationCloses: Joi.date(),
    sponsor: Joi.string(),
    sponsorWWWLink: Joi.string(),
    waitListed: Joi.array()
        .unique()
        .items(registrationSchema)
        .default([]),
    volume: Joi.number()
        .integer()
        .required()
});
exports.feedbackSchema = Joi.object().keys({
    feedback: Joi.string()
        .min(1)
        .required()
});
//# sourceMappingURL=schemas.js.map