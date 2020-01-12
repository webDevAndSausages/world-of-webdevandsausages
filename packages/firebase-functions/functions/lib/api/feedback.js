"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const db_1 = require("../services/db");
const schemas_1 = require("./schemas");
const joi_1 = require("joi");
const fluture_1 = require("fluture");
const http_errors_1 = require("http-errors");
const ramda_1 = require("ramda");
const utils_1 = require("../utils");
const feedbackIsOpen = ({ datetime }) => utils_1.isWithin24Hours(datetime);
exports.addFeedback = (request, response, next) => {
    const eventId = request.params.id;
    const feedback = request.body.feedback;
    fluture_1.default((rej, res) => {
        const { error } = joi_1.validate(request.body, schemas_1.feedbackSchema);
        return error ? rej(new http_errors_1.UnprocessableEntity(error.message)) : res(null);
    })
        .chain(() => {
        return fluture_1.tryP(() => db_1.eventsRef.doc(eventId).get())
            .map(utils_1.docDataOrNull)
            .chain(event => {
            if (!event) {
                return fluture_1.reject(new http_errors_1.NotFound());
            }
            else if (!feedbackIsOpen(event)) {
                return fluture_1.reject(new http_errors_1.Forbidden());
            }
            const updatedFeedback = ramda_1.concat(ramda_1.propOr([], 'feedback', event), [
                feedback
            ]);
            return fluture_1.tryP(() => db_1.eventsRef.doc(eventId).update({ feedback: updatedFeedback }));
        });
    })
        .fork(error => next(error), () => response.json({ result: 'success' }));
};
//# sourceMappingURL=feedback.js.map