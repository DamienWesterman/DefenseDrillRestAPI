-- TODO: Create named constraints for everything
CREATE TABLE drills(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE categories(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(511) NOT NULL
);

CREATE TABLE sub_categories(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(511) NOT NULL
);

CREATE TABLE drill_category_join(
    drill_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    PRIMARY KEY (drill_id, category_id),
    FOREIGN KEY (drill_id) REFERENCES drills(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE TABLE drill_sub_category_join(
    drill_id INTEGER NOT NULL,
    sub_category_id INTEGER NOT NULL,
    PRIMARY KEY (drill_id, sub_category_id),
    FOREIGN KEY (drill_id) REFERENCES drills(id) ON DELETE CASCADE,
    FOREIGN KEY (sub_category_id) REFERENCES sub_categories(id) ON DELETE CASCADE
);

CREATE TABLE related_drills(
    primary_drill_id INTEGER NOT NULL,
    related_drill_id INTEGER NOT NULL,
    PRIMARY KEY (primary_drill_id, related_drill_id),
    FOREIGN KEY (primary_drill_id) REFERENCES drills(id) ON DELETE CASCADE,
    FOREIGN KEY (related_drill_id) REFERENCES drills(id) ON DELETE CASCADE
);

CREATE TABLE instructions(
    drill_id INTEGER NOT NULL,
    number INTEGER NOT NULL,
    description VARCHAR(511) NOT NULL,
    -- Pipe delimited string of steps
    steps VARCHAR(4095) NOT NULL,
    video_id VARCHAR(127),
    PRIMARY KEY (drill_id, number),
    FOREIGN KEY (drill_id) REFERENCES drills(id) ON DELETE CASCADE
);
