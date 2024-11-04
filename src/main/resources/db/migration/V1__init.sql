CREATE TABLE drills(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE groups(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(511)
);

CREATE TABLE sub_groups(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(511)
);

CREATE TABLE drill_group_join(
    drill_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    PRIMARY KEY (drill_id, group_id),
    FOREIGN KEY (drill_id) REFERENCES drills(id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
);

CREATE TABLE drill_sub_group_join(
    drill_id INTEGER NOT NULL,
    sub_group_id INTEGER NOT NULL,
    PRIMARY KEY (drill_id, sub_group_id),
    FOREIGN KEY (drill_id) REFERENCES drills(id) ON DELETE CASCADE,
    FOREIGN KEY (sub_group_id) REFERENCES sub_groups(id) ON DELETE CASCADE
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
    description VARCHAR(1023),
    -- Pipe delimited string of steps
    steps VARCHAR(4095) NOT NULL,
    video_id VARCHAR(127),
    PRIMARY KEY (drill_id, number),
    FOREIGN KEY (drill_id) REFERENCES drills(id) ON DELETE CASCADE
);
