CREATE TABLE common_routes (
    id BIGSERIAL PRIMARY KEY,
    origin VARCHAR(150) NOT NULL,
    destination VARCHAR(150) NOT NULL,
    load INTEGER NOT NULL
);

CREATE TABLE path_between_capitals (
    id BIGSERIAL PRIMARY KEY,
    origin VARCHAR(150) NOT NULL,
    destination VARCHAR(150) NOT NULL,
    distance INTEGER NOT NULL
);