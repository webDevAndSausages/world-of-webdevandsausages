"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Nexmo = require('nexmo');
const fluture_1 = require("fluture");
const __1 = require("..");
const nexmo = new Nexmo({
    apiKey: __1.config.NEXMO.KEY,
    apiSecret: __1.config.NEXMO.SECRET
});
exports.sendSms = (text, phoneNumber) => {
    return fluture_1.default((rej, res) => {
        return nexmo.message.sendSms('WD&S', phoneNumber, text, (err, result) => {
            if (err)
                return rej(err);
            return res(result);
        });
    });
};
//# sourceMappingURL=sms.js.map