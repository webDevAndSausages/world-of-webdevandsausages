create table contact (
   id                  bigserial primary key,
   email               varchar(255) unique not null,
   first_name          varchar(255),
   last_name           varchar(255),
   phone               varchar(255),
   unsubscribed        bool          not null default false,
   created_on          timestamp     not null default current_timestamp,
   updated_on          timestamp     not null default current_timestamp
);

create or replace function add_contact()
    returns trigger as
$body$
begin
    insert into contact (email, first_name, last_name)
    values (new.email, new.first_name, new.last_name)
    on conflict on constraint contact_email_key
    do nothing;
    return new;
end
$body$
language plpgsql
volatile
cost 100;


create trigger registration_added
    after insert
    on participant
    for each row
execute procedure add_contact();