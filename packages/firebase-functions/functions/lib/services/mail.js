"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const sgMail = require('@sendgrid/mail');
const fluture_1 = require("fluture");
const __1 = require("..");
exports.sendMail = (msg) => {
    sgMail.setApiKey(__1.config.SENDGRID_KEY);
    return fluture_1.tryP(() => sgMail.send(msg));
};
//# sourceMappingURL=mail.js.map