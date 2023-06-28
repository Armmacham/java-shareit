DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id    int          NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  varchar(200) NOT NULL,
    email varchar(200) NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           int          NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description  varchar(200) NOT NULL,
    requestor_id int          NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT requests_requestor_id FOREIGN KEY (requestor_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id           int          NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         varchar(200) NOT NULL,
    description  varchar(200) NOT NULL,
    is_available boolean      NOT NULL,
    owner_id     int          NOT NULL,
    request_id   int,
    CONSTRAINT items_owner_id FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT items_request_id FOREIGN KEY (request_id) REFERENCES requests (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         int                         NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date timestamp without time zone not null,
    end_date   timestamp without time zone not null,
    item_id    int,
    booker_id  int,
    status     varchar,
    CONSTRAINT bookings_item_id FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT bookings_booker_id FOREIGN KEY (booker_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    id        int          NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text      varchar(300) NOT NULL,
    item_id   int NOT NULL,
    created   timestamp without time zone not null,
    author_id int NOT NULL,
    CONSTRAINT comment_item_id FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT comment_author_id FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);



