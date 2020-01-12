"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const randomWord = require('random-word');
const sms_1 = require("../services/sms");
const mail_1 = require("../services/mail");
const db_1 = require("../services/db");
const fluture_1 = require("fluture");
const utils_1 = require("../utils");
const http_errors_1 = require("http-errors");
const ramda_1 = require("ramda");
const moment = require("moment");
const sendCodeByEmail = (data, pass, name) => {
    const msg = {
        to: data.email,
        from: 'richard.vancamp@gmail.com',
        subject: 'Web dev & sausages admin',
        template_id: 'f382f085-52eb-49ba-b7c6-ba58b9b61e97',
        substitutions: {
            token: pass,
            user: name
        }
    };
    return mail_1.sendMail(msg);
};
const sendCodeBySms = (data, pass) => sms_1.sendSms(pass, data.phoneNumber);
exports.getCodeByEmailOrSms = (req, res, next) => {
    const name = ramda_1.compose(ramda_1.toLower, ramda_1.pathOr('', ['params', 'id']))(req);
    const bySms = req.query.method && req.query.method === 'sms';
    const pass = randomWord();
    const expires = moment().add(10, 'minutes').unix();
    const sender = bySms ? sendCodeBySms : sendCodeByEmail;
    return fluture_1.tryP(() => db_1.adminsRef.doc(name).get())
        .map(utils_1.docDataOrNull)
        .chain((data) => {
        if (data && data.phoneNumber) {
            return fluture_1.both(fluture_1.tryP(() => db_1.adminsRef.doc(name).update({ pass, expires })), sender(data, pass, name));
        }
        return fluture_1.reject(new http_errors_1.Unauthorized());
    })
        .fork((err) => {
        console.log(err);
        next(new http_errors_1.InternalServerError());
    }, () => res.status(200).json({ status: 'success' }));
};
const isValidPass = (data, pass) => {
    const unixNow = moment().unix();
    return data.pass && data.expires && data.pass === pass && unixNow <= data.expires;
};
exports.auth = (req, res, next) => {
    const name = req.session.user;
    console.log(req.session);
    fluture_1.tryP(() => db_1.adminsRef.doc(name).get())
        .map(utils_1.docDataOrNull)
        .chain((data) => {
        if (!data) {
            req.session = null;
            return fluture_1.reject(new http_errors_1.Unauthorized());
        }
        return fluture_1.of({ name, admin: true });
    })
        .fork((err) => next(err), (data) => res.status(200).send({ data }));
};
exports.login = (req, res, next) => {
    const pass = req.body.pass;
    const name = req.body.name;
    fluture_1.tryP(() => db_1.adminsRef.doc(name).get())
        .map(utils_1.docDataOrNull)
        .chain((data) => {
        if (isValidPass(data, pass)) {
            req.session.user = name;
            return fluture_1.of({ name, admin: true });
        }
        return fluture_1.reject(new http_errors_1.Unauthorized());
    })
        .fork((err) => next(err), (data) => res.status(200).send({ data }));
};
exports.logout = (req, res, next) => {
    req.session = null;
    res.status(200).json({ user: null });
};
//# sourceMappingURL=auth.js.map