-- V 1.0 Create tables
CREATE TABLE subscription
(
    id          SERIAL PRIMARY KEY,
    link        VARCHAR(255) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR
);

CREATE UNIQUE INDEX idx_subscription_name ON subscription (name);

CREATE TABLE items
(
    id              SERIAL       NOT NULL,
    title           VARCHAR(255) NOT NULL,
    link            VARCHAR(255) NOT NULL,
    description     VARCHAR,
    publication     timestamptz  NOT NULL,
    subscription_id INT          NOT NULL
        CONSTRAINT fk_item_subscription_id REFERENCES subscription (id) ON DELETE CASCADE
);

CREATE TABLE channel
(
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    link        VARCHAR(255) NOT NULL,
    description VARCHAR,
    items       VARCHAR[]
);