"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const joi_1 = require("joi");
const schemas_1 = require("./schemas");
const fluture_1 = require("fluture");
const createError = require("http-errors");
const utils_1 = require("../utils");
const db_1 = require("../services/db");
const ramda_1 = require("ramda");
const mail_1 = require("../services/mail");
exports.safeData = (schema, withErrors, withId) => doc => {
    const data = utils_1.docDataOrNull(doc);
    const id = utils_1.docIdOrNull(doc);
    if (ramda_1.either(ramda_1.isNil, ramda_1.isEmpty)(data))
        return fluture_1.of(null);
    const { error } = joi_1.validate(data, schema);
    const dataWithExtras = [data];
    if (withErrors && error) {
        dataWithExtras.push({
            validationError: error.message ? error.message : error
        });
    }
    if (withId && id) {
        dataWithExtras.push({ id });
    }
    const result = ramda_1.mergeAll(dataWithExtras);
    return fluture_1.of(result);
};
exports.getCollection = (dbRef, schema) => (req, res, next) => {
    return fluture_1.tryP(() => dbRef.get())
        .chain(docsSnapshots => {
        const docs = [];
        docsSnapshots.forEach(d => docs.push(d));
        return ramda_1.traverse(fluture_1.of, exports.safeData(schema, true, true), docs);
    })
        .map(ramda_1.filter(ramda_1.complement(ramda_1.isNil)))
        .fork(error => next(createError(500, error)), data => res.json({ data }));
};
exports.getAllParticipants = exports.getCollection(db_1.participantsRef, schemas_1.participantSchema);
exports.upsertParticipant = (body) => () => {
    const currentRef = db_1.participantsRef.doc(body.email);
    const participant = utils_1.addInsertionDate(body);
    return fluture_1.tryP(() => currentRef.set(participant, { merge: true }));
};
exports.addParticipant = (request, response, next) => {
    return (fluture_1.default((rej, res) => {
        const { error } = joi_1.validate(request.body, schemas_1.participantSchema);
        return error ? rej(createError(422, error.message)) : res(null);
    })
        .chain(exports.upsertParticipant(request.body))
        .chain(() => fluture_1.tryP(() => db_1.participantsRef.doc(request.body.email).get()))
        .map(utils_1.docDataOrNull)
        .chain(result => {
        const msg = {
            to: result.email,
            from: 'richard.vancamp@gmail.com',
            subject: "Wed dev & sausage's mailing list confirmation",
            template_id: '487d4c85-5cd0-4602-80e2-5120d2483f76'
        };
        return mail_1.sendMail(msg);
    })
        .fork(error => {
        console.error('Failed to save participant to db: ', error);
        next(new createError.InternalServerError());
    }, () => response.status(201).json({ success: true })));
};
exports.removeParticipant = (request, response, next) => {
    const email = request.params.email;
    const participant = db_1.participantsRef.doc(email);
    fluture_1.tryP(() => participant.get())
        .chain(doc => {
        if (doc && doc.exists) {
            return fluture_1.tryP(() => participant.delete());
        }
        return fluture_1.reject(new createError.NotFound());
    })
        .fork(error => next(error), () => response.status(202).json({ success: true }));
};
//# sourceMappingURL=participants.js.map