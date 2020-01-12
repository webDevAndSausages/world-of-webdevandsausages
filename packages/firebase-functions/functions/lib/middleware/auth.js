"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const http_errors_1 = require("http-errors");
exports.authorizeAdmin = (req, res, next) => {
    if (!req.session.user) {
        console.error('No session cookie was present.', req.session);
        next(new http_errors_1.Unauthorized());
        return;
    }
    next();
};
//# sourceMappingURL=auth.js.map