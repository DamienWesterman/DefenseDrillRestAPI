-- Migration to add timestamp field to drills, categories, and sub-categories
ALTER table drills ADD COLUMN update_timestamp
    BIGINT
    NOT NULL
    DEFAULT 0;

ALTER table categories ADD COLUMN update_timestamp
    BIGINT
    NOT NULL
    DEFAULT 0;

ALTER table sub_categories ADD COLUMN update_timestamp
    BIGINT
    NOT NULL
    DEFAULT 0;
