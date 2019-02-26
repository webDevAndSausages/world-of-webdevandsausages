alter table event
add column volume integer unique not null,
add column sponsor_link varchar(255);