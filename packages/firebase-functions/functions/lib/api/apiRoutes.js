"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = require("express");
const register_1 = require("./register");
const participants_1 = require("./participants");
const apiErrors_1 = require("./apiErrors");
const cancelRegistration_1 = require("./cancelRegistration");
const auth_1 = require("../middleware/auth");
const auth_2 = require("./auth");
const events_1 = require("./events");
const feedback_1 = require("./feedback");
const router = express_1.Router();
/* EVENTS */
router.get('/events', auth_1.authorizeAdmin, events_1.getAllEvents);
router.get('/events/current', events_1.getCurrentEvent);
router.post('/events', auth_1.authorizeAdmin, events_1.createEvent);
router.put('/events/:id', auth_1.authorizeAdmin, events_1.updateEvent);
router.delete('/events/:id', auth_1.authorizeAdmin, events_1.removeEvent);
router.post('/events/:id/feedback', feedback_1.addFeedback);
/* PARTICIPANTS */
router.get('/participants', auth_1.authorizeAdmin, participants_1.getAllParticipants);
router.post('/participants', participants_1.addParticipant);
router.delete('/participants/:email', auth_1.authorizeAdmin, participants_1.removeParticipant);
/* REGISTRATION */
router.post('/register/:eventId', register_1.register);
router.get('/register/:eventId', register_1.verifyRegistration);
router.delete('/register/:eventId', cancelRegistration_1.cancelRegistration);
/* AUTH */
router.get('/temppass/:id', auth_2.getCodeByEmailOrSms);
router.get('/auth', auth_1.authorizeAdmin, auth_2.auth);
router.post('/auth', auth_2.login);
router.delete('/auth', auth_2.logout);
/* ERROR */
router.use(apiErrors_1.apiErrorHandler);
exports.default = router;
//# sourceMappingURL=apiRoutes.js.map