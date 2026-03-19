-- -----------------------------------------------
-- DROP (cascade removes all dependencies)
-- -----------------------------------------------
DROP TABLE IF EXISTS common_routes CASCADE;
DROP TABLE IF EXISTS path_between_capitals CASCADE;
DROP TABLE IF EXISTS capitals CASCADE;

-- -----------------------------------------------
-- CREATE TABLES
-- -----------------------------------------------
CREATE TABLE capitals (
    id          VARCHAR(2)      PRIMARY KEY, -- State abbreviation
    name        VARCHAR(50)     NOT NULL,
    latitude    DECIMAL(10, 8)  NOT NULL,
    longitude   DECIMAL(11, 8)  NOT NULL
);

CREATE TABLE path_between_capitals (
    id              BIGSERIAL       PRIMARY KEY,
    origin_id       VARCHAR(2)    NOT NULL,
    destination_id  VARCHAR(2)    NOT NULL,
    distance        INTEGER         NOT NULL,   -- km
    has_railway     BOOLEAN         DEFAULT FALSE,
    FOREIGN KEY (origin_id)        REFERENCES capitals(id),
    FOREIGN KEY (destination_id)   REFERENCES capitals(id)
);

CREATE TABLE common_routes (
    id              BIGSERIAL       PRIMARY KEY,
    origin_id       VARCHAR(2)    NOT NULL,
    destination_id  VARCHAR(2)    NOT NULL,
    load            INTEGER         NOT NULL,   -- daily freight count
    FOREIGN KEY (origin_id)        REFERENCES capitals(id),
    FOREIGN KEY (destination_id)   REFERENCES capitals(id)
);
