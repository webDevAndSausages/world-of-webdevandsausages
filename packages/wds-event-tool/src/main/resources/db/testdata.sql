-- Init
INSERT INTO "public"."event" ("id", "name", "contact", "date", "details", "location", "status", "max_participants", "registration_opens") VALUES (DEFAULT, 'koe1', 'esa', '2018-12-19 09:06:12.595000', 'jotain', 'Tampere', 'OPEN', 2, '2018-12-15');
INSERT INTO "public"."event" ("id", "name", "contact", "date", "details", "location", "status", "max_participants", "registration_opens") VALUES (DEFAULT, 'toinen', 'pena', '2018-12-19 09:07:03.020000', 'koe', 'Hervanta', 'PLANNING', 2, '2019-02-01');
INSERT INTO "public"."participant" ("id", "first_name", "last_name", "email", "affiliation", "verification_token", "order_number", "event_id", "status") VALUES (DEFAULT, 'John1', 'Smith', 'john@smith.com', 'test', '1234', 1000, 1, 'REGISTERED');
INSERT INTO "public"."participant" ("id", "first_name", "last_name", "email", "affiliation", "verification_token", "order_number", "event_id", "status") VALUES (DEFAULT, 'John2', 'Smith', 'john@smith.com', 'test', '1234', 2000, 1, 'REGISTERED');
INSERT INTO "public"."participant" ("id", "first_name", "last_name", "email", "affiliation", "verification_token", "order_number", "event_id", "status") VALUES (DEFAULT, 'John3', 'Smith', 'john@smith.com', 'test', '1234', 1000, 2, 'REGISTERED');
INSERT INTO "public"."participant" ("id", "first_name", "last_name", "email", "affiliation", "verification_token", "order_number", "event_id", "status") VALUES (DEFAULT, 'John4', 'Smith', 'john@smith.com', 'test', '1234', 2000, 2, 'REGISTERED');
INSERT INTO "public"."participant" ("id", "first_name", "last_name", "email", "affiliation", "verification_token", "order_number", "event_id", "status") VALUES (DEFAULT, 'John5', 'Smith', 'john@smith.com', 'test', '1234', 3000, 2, 'REGISTERED');
INSERT INTO "public"."participant" ("id", "first_name", "last_name", "email", "affiliation", "verification_token", "order_number", "event_id", "status") VALUES (DEFAULT, 'John6', 'Smith', 'john@smith.com', 'test', '1234', 3000, 1, 'REGISTERED');

