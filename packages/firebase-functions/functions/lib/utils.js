"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const sanctuary_1 = require("sanctuary");
const { env: flutureEnv } = require('fluture-sanctuary-types');
const ramda_1 = require("ramda");
const moment = require("moment-timezone");
exports.isNot = (a) => (b) => ramda_1.complement(ramda_1.equals)(a, b);
moment()
    .tz('Europe/Helsinki')
    .format();
const checkTypes = process.env.NODE_ENV !== 'production';
exports.S = sanctuary_1.create({ checkTypes, env: sanctuary_1.env.concat(flutureEnv) });
// safety utils
exports.docDataOrNull = doc => (!doc || !doc.exists ? null : doc.data());
exports.docIdOrNull = doc => (!doc || !doc.exists ? null : doc.id);
exports.areValidResults = ramda_1.compose(ramda_1.not, ramda_1.either(ramda_1.isNil, ramda_1.has('error')));
exports.notNil = ramda_1.compose(ramda_1.not, ramda_1.isNil);
// date utils
exports.addInsertionDate = ramda_1.assoc('insertedOn', moment().format('YYYY-MM-DD HH:mm'));
exports.formatDate = date => `${moment(date)
    .add(2, 'hours')
    .format('dddd, MMMM Do YYYY, HH:mm')}`;
exports.isWithin24Hours = datetime => {
    const start = moment(datetime);
    const end = moment(datetime).add(24, 'hours');
    return moment().isBetween(start, end);
};
// mail utils
exports.createMailMsg = ramda_1.evolve({
    to: ramda_1.identity,
    from: v => (v ? v : 'richard.vancamp@gmail.com'),
    subject: s => `Web Dev & Sausages ${s || ''}`,
    text: ramda_1.identity
});
exports.filterOutTokenAndEmail = (email, token) => queue => queue.filter(reg => !(reg.email === email && reg.verificationToken === token));
exports.findByEmailAndPassword = (email, verificationToken) => ramda_1.find(ramda_1.both(ramda_1.propEq('email', email), ramda_1.propEq('verificationToken', verificationToken)));
// other
exports.findIndexOfRegistration = registration => ramda_1.findIndex(ramda_1.equals(registration));
exports.propHasLength = prop => ramda_1.compose(ramda_1.flip(ramda_1.gt)(0), ramda_1.length, ramda_1.propOr([], prop));
//# sourceMappingURL=utils.js.map