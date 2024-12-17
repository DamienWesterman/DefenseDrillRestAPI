-- TODO: Create named constraints for everything
-- Changes to any constraints/constraint names must be reflected in service/ErrorMessageUtils.java
-- NOT NULL constraints do not need to be named as @NotNull annotations will catch it
CREATE TABLE drills(
    id SERIAL
        PRIMARY KEY,
    name VARCHAR(255)
        NOT NULL
        CONSTRAINT constraint_drills_unique_name UNIQUE
);

CREATE TABLE categories(
    id SERIAL
        PRIMARY KEY,
    name VARCHAR(255)
        NOT NULL
        CONSTRAINT constraint_categories_unique_name UNIQUE,
    description VARCHAR(511)
        NOT NULL
);

CREATE TABLE sub_categories(
    id SERIAL
        PRIMARY KEY,
    name VARCHAR(255)
        NOT NULL
        CONSTRAINT constraint_sub_categories_unique_name UNIQUE,
    description VARCHAR(511)
        NOT NULL
);

CREATE TABLE drill_category_join(
    drill_id INTEGER
        NOT NULL,
    category_id INTEGER
        NOT NULL,
    PRIMARY KEY (drill_id, category_id),
    CONSTRAINT constraint_dcjoin_fk_drill_id
        FOREIGN KEY (drill_id)
        REFERENCES drills(id)
        ON DELETE CASCADE,
    CONSTRAINT constraint_dcjoin_fk_category_id
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON DELETE CASCADE
);

CREATE TABLE drill_sub_category_join(
    drill_id INTEGER
        NOT NULL,
    sub_category_id INTEGER
        NOT NULL,
    PRIMARY KEY (drill_id, sub_category_id),
    CONSTRAINT constraint_dscjoin_fk_drill_id
        FOREIGN KEY (drill_id)
        REFERENCES drills(id)
        ON DELETE CASCADE,
    CONSTRAINT constraint_dscjoin_fk_sub_category_id
        FOREIGN KEY (sub_category_id)
        REFERENCES sub_categories(id)
        ON DELETE CASCADE
);

CREATE TABLE related_drills(
    primary_drill_id INTEGER
        NOT NULL,
    related_drill_id INTEGER
        NOT NULL,
    PRIMARY KEY (primary_drill_id, related_drill_id),
    CONSTRAINT constraint_rd_fk_primary_drill
        FOREIGN KEY (primary_drill_id)
        REFERENCES drills(id)
        ON DELETE CASCADE,
    CONSTRAINT constraint_rd_fk_related_drill
        FOREIGN KEY (related_drill_id)
        REFERENCES drills(id)
        ON DELETE CASCADE
);

CREATE TABLE instructions(
    drill_id INTEGER
        NOT NULL,
    number INTEGER
        NOT NULL,
    description VARCHAR(511)
        NOT NULL,
    -- Pipe delimited string of steps
    steps VARCHAR(4095)
        NOT NULL,
    video_id VARCHAR(127),
    PRIMARY KEY (drill_id, number),
    CONSTRAINT constraint_instructions_fk_drill_id
        FOREIGN KEY (drill_id)
        REFERENCES drills(id)
        ON DELETE CASCADE
);
