DROP TABLE IF EXISTS beer_price;
DROP TABLE IF EXISTS store;
DROP TABLE IF EXISTS beer;

CREATE TABLE beer (
    id BIGINT IDENTITY PRIMARY KEY,
    brand VARCHAR(30) NOT NULL,
    type VARCHAR(30),
    volume FLOAT(53) DEFAULT 0.5);

CREATE TABLE store (
    id BIGINT IDENTITY PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    city VARCHAR(30) NOT NULL,
    street VARCHAR(60) NOT NULL);

CREATE TABLE beer_price (
    beer_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    price_amount NUMERIC(38, 2) NOT NULL,
    price_currency VARCHAR(255) DEFAULT 'PLN',
    PRIMARY KEY (beer_id, store_id),
    FOREIGN KEY (beer_id) REFERENCES beer(id),
    FOREIGN KEY (store_id) REFERENCES store(id)
    );

CREATE TABLE users (
    id BIGINT IDENTITY PRIMARY KEY,
    username VARCHAR(32) NOT NULL,
    password VARCHAR(255) NOT NULL,
    account_non_locked BOOLEAN,
    roles SMALLINT ARRAY
);