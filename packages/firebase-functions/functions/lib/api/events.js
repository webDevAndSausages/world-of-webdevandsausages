"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const participants_1 = require("./participants");
const db_1 = require("../services/db");
const schemas_1 = require("./schemas");
const joi_1 = require("joi");
const fluture_1 = require("fluture");
const http_errors_1 = require("http-errors");
const ramda_1 = require("ramda");
const moment_timezone_1 = require("moment-timezone/moment-timezone");
const utils_1 = require("../utils");
exports.getAllEvents = participants_1.getCollection(db_1.eventsRef, schemas_1.eventSchema);
const isFutureEvent = ramda_1.compose((date) => moment_timezone_1.default().isSameOrBefore(moment_timezone_1.default(date).add(26, 'hours')), ramda_1.prop('datetime'));
const transformations = {
    registered: ramda_1.length,
    waitListed: ramda_1.length,
    feedback: null,
    feedbackPass: null
};
const safeHead = (array) => (array.length > 0 ? array[0] : {});
const transformForPublicApi = (current) => {
    const currentEvent = ramda_1.compose(ramda_1.evolve(transformations), safeHead)(current);
    const feedbackOpen = ramda_1.compose(ramda_1.ifElse(ramda_1.has('datetime'), ramda_1.compose(utils_1.isWithin24Hours, ramda_1.prop('datetime')), ramda_1.always(false)))(currentEvent);
    return { currentEvent, feedbackOpen };
};
exports.getCurrentEvent = (req, res, next) => {
    return fluture_1.tryP(() => db_1.eventsRef.get())
        .chain((docsSnapshots) => {
        const docs = [];
        docsSnapshots.forEach((d) => docs.push(d));
        return ramda_1.traverse(fluture_1.of, participants_1.safeData(schemas_1.eventSchema, false, true), docs);
    })
        .chain((events) => {
        const current = ramda_1.filter(isFutureEvent, events);
        return fluture_1.of(transformForPublicApi(current));
    })
        .fork((error) => next(error), (data) => res.json({ data }));
};
exports.createEvent = (request, respose, next) => {
    const newEvent = request.body;
    fluture_1.default((rej, res) => {
        const { error, value } = joi_1.validate(newEvent, schemas_1.eventSchema);
        return error ? rej(new http_errors_1.UnprocessableEntity(error.message)) : res(value);
    })
        .chain((event) => {
        return fluture_1.tryP(() => db_1.eventsRef.add(event)).chain((docRef) => {
            if (docRef.id) {
                return fluture_1.tryP(() => db_1.eventsRef.doc(docRef.id).get()).map(utils_1.docDataOrNull);
            }
            return fluture_1.reject(new http_errors_1.InternalServerError());
        });
    })
        .fork((error) => next(error), (data) => respose.json({ data }));
};
exports.updateEvent = (request, respose, next) => {
    const eventId = request.params.id;
    const updates = request.body;
    fluture_1.default((rej, res) => {
        const { error } = joi_1.validate(updates, schemas_1.eventSchema, { noDefaults: true });
        return error ? rej(new http_errors_1.UnprocessableEntity(error.message)) : res(null);
    })
        .chain(() => {
        return fluture_1.tryP(() => db_1.eventsRef.doc(eventId).update(updates)).chain(() => {
            return fluture_1.tryP(() => db_1.eventsRef.doc(eventId).get()).map(utils_1.docDataOrNull);
        });
    })
        .fork((error) => next(error), (data) => respose.json({ data }));
};
exports.removeEvent = (request, response, next) => {
    const eventId = request.params.id;
    const event = db_1.eventsRef.doc(eventId);
    fluture_1.tryP(() => event.get())
        .chain((doc) => {
        if (doc && doc.exists) {
            return fluture_1.tryP(() => event.delete());
        }
        return fluture_1.reject(new http_errors_1.NotFound());
    })
        .fork((error) => next(error), () => response.status(202).json({ success: true }));
};
//# sourceMappingURL=events.js.map