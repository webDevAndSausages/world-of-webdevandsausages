alter table participant add constraint participant_verification_token_unique unique (verification_token);
