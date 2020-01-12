"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const ramda_1 = require("ramda");
const parseError = ramda_1.pick(['status', 'message']);
exports.apiErrorHandler = (err, req, res, next) => {
    console.log('Error', err.toString());
    const { status, message } = parseError(err);
    res.status(status || 500).json({ error: message || 'Oops, a goof happened.' });
};
//# sourceMappingURL=apiErrors.js.map