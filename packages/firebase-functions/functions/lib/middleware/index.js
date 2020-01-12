"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const cors = require('cors');
const cookieSession = require("firebase-cookie-session");
const _1 = require("../");
const setMiddleware = app => {
    // credentials:true needed for cookies
    app.use(cors({ origin: true, credentials: true }));
    app.options('*', cors());
    app.use(
    // firebase fucntions only allow one cookie with the name __session
    cookieSession({
        secure: false,
        httpOnly: false,
        keys: [_1.config.GOOGLE.SECRET],
        maxAge: 72 * 60 * 60 * 1000 // 72 hours
    }));
    return app;
};
exports.default = setMiddleware;
//# sourceMappingURL=index.js.map