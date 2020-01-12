"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const axios_1 = require("axios");
const fluture_1 = require("fluture");
const _1 = require("../");
const axiosF = fluture_1.encaseP2(axios_1.default.post);
exports.notifySlack = (email) => {
    const data = { text: `${email} joined the mailing list!` };
    axiosF(_1.config.SLACK_URL, data).fork((err) => console.log('An error occurred dispatching a message to slack: ', err), () => console.log(`Slack has received a notification about ${email}`));
};
//# sourceMappingURL=slack.js.map