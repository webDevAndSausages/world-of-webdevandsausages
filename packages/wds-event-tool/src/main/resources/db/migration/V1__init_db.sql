/**
  * Event status explanations:
  * PLANNING:
  *  - not visible
  * VISIBLE:
  *  - is visible, but registration is not open
  * OPEN:
  *  - visible
  *  - registration open
  *  - cancellation open
  * OPEN_FULL:
  *  - visible
  *  - registration not possible
  *  - cancellation possible
  * CLOSED_WITH_FEEDBACK:
  *  - visible
  *  - feedback opened
  *  - registration not possible
  *  - cancellation not possible
  * CLOSED:
  *  - not visible
  *  - feedback not possible
  *  - registration not possible
  *  - cancellation not possible
  * CANCELLED:
  *  - not visible
  *  - feedback not possible
  *  - registration not possible
  *  - cancellation not possible
  *  - not displayed in "past events" -list because planning/registration has been interrupted
  */
create type event_status as enum (
  'PLANNING',
  'VISIBLE',
  'OPEN',
  'OPEN_WITH_WAITLIST',
  'OPEN_FULL',
  'CLOSED_WITH_FEEDBACK',
  'CLOSED',
  'CANCELLED'
);

create table event (
  id                  bigserial primary key,
  name                varchar(255)  not null,
  sponsor             varchar(255),
  contact             varchar(255),
  date                timestamp     not null,
  details             varchar(1024) not null,
  location            varchar(255)  not null,
  status              event_status  not null,
  max_participants    integer       not null,
  registration_opens  timestamp     not null
);

/**
 * Participant status expanations:
 * ORGANIZER:
 *  - Registered, but not counted in max participants
 * REGISTERED:
 *  - Normally registered user
 * WAIT_LISTED:
 *  - Not registered yet, but on the waiting list
 * CANCELLED:
 *  - Cancelled from event, not counted in max participants
 */
create type participant_status as enum (
  'ORGANIZER',
  'REGISTERED',
  'WAIT_LISTED',
  'CANCELLED'
);

create table participant (
  id                 bigserial primary key,
  first_name         varchar(255),
  last_name          varchar(255),
  email              varchar(255)       not null,
  affiliation        varchar(255)       not null,
  verification_token varchar(255)       not null,
  order_number       int                not null, /* Should be unique within event, do it in the code */
  event_id           bigserial          not null references event (id) on update cascade on delete restrict,
  status             participant_status not null
);

/**
 * If participant table changes somehow
 *  - get events with status OPEN or OPEN_WITH_WAITLIST
 *  - count users with status REGISTERED for each of the events filtered
 *  - if count >= event.max_participants then change status to OPEN_WITH_WAITLIST
 *  - else change status to OPEN
 */

create or replace function update_event_status()
  returns void as
$body$
begin
  update event
  set status = 'OPEN_WITH_WAITLIST'
  where event.status = 'OPEN' and
        (select count(*)
         from participant as p
         where p.event_id = event.id and p.status = 'REGISTERED') >= max_participants;

  update event
  set status = 'OPEN'
  where event.status = 'OPEN_WITH_WAITLIST' and
        (select count(*)
         from participant as p
         where p.event_id = event.id and p.status = 'REGISTERED') < max_participants;
end
$body$
language plpgsql
volatile
cost 100;

create or replace function event_status_possibly_changes()
  returns trigger as
$body$
begin
  execute update_event_status();
  return new;
end
$body$
language plpgsql
volatile
cost 100;

create or replace function check_if_max_participants_changed()
  returns trigger as
$body$
begin
  if new.max_participants <> old.max_participants
  then
    execute update_event_status();
  end if;
  return new;
end
$body$
language plpgsql
volatile
cost 100;

create trigger participant_status_changed
  after update or insert or delete
  on participant
  for each statement
execute procedure event_status_possibly_changes();

/* Also update status if max participants is changed */
create trigger max_participants_changed
  after update
  on event
  for each row
execute procedure check_if_max_participants_changed();
