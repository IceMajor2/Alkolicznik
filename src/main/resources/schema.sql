CREATE TABLE IF NOT EXISTS beer (
    id SERIAL PRIMARY KEY,
    brand VARCHAR(30) NOT NULL,
    type VARCHAR(30),
    volume FLOAT(53) DEFAULT 0.5);

CREATE TABLE IF NOT EXISTS store (
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    city VARCHAR(30) NOT NULL,
    street VARCHAR(60) NOT NULL);

CREATE TABLE IF NOT EXISTS beer_price (
    beer_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    price_amount NUMERIC(38, 2) NOT NULL,
    price_currency VARCHAR(255) DEFAULT 'PLN',
    PRIMARY KEY (beer_id, store_id),
    FOREIGN KEY (beer_id) REFERENCES beer(id),
    FOREIGN KEY (store_id) REFERENCES store(id)
    );

CREATE TABLE IF NOT EXISTS beer_image (
    beer_id BIGINT NOT NULL,
    url VARCHAR(255) NOT NULL,
    remote_id VARCHAR(255),
    image_component BYTEA,
    PRIMARY KEY (beer_id),
    FOREIGN KEY (beer_id) REFERENCES beer(id)
);

CREATE TABLE IF NOT EXISTS store_image (
	id BIGINT NOT NULL,
	store_name VARCHAR(255) NOT NULL UNIQUE,
    url VARCHAR(255) NOT NULL,
    remote_id VARCHAR(255),
    image_component BYTEA,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) CHECK (role in ('USER', 'ACCOUNTANT', 'ADMIN')),
    PRIMARY KEY (id)
);