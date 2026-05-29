CREATE TABLE profile
(
    account_id       INTEGER      NOT NULL,
    uuid             UUID         NOT NULL,
    first_name       VARCHAR(255) NOT NULL,
    father_name      VARCHAR(255) NOT NULL,
    grandfather_name VARCHAR(255) NOT NULL,
    family_name      VARCHAR(255) NOT NULL,
    national_id      VARCHAR(255) NOT NULL,
    mobile           VARCHAR(255) NOT NULL,
    CONSTRAINT pk_profile PRIMARY KEY (account_id)
);

ALTER TABLE profile
    ADD CONSTRAINT uc_profile_mobile UNIQUE (mobile);

ALTER TABLE profile
    ADD CONSTRAINT uc_profile_national UNIQUE (national_id);

ALTER TABLE profile
    ADD CONSTRAINT uc_profile_uuid UNIQUE (uuid);

ALTER TABLE profile
    ADD CONSTRAINT FK_PROFILE_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);