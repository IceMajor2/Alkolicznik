DROP TABLE IF EXISTS beer_price;
DROP TABLE IF EXISTS beer_image;
DROP TABLE IF EXISTS store_image;
DROP TABLE IF EXISTS store;
DROP TABLE IF EXISTS beer;
DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS users_id_seq;
DROP SEQUENCE IF EXISTS store_img_id_seq;
DROP SEQUENCE IF EXISTS store_seq;
DROP SEQUENCE IF EXISTS beer_seq;

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

CREATE TABLE beer_image (
    beer_id BIGINT NOT NULL,
    remote_id VARCHAR(255),
    url VARCHAR(255),
    image_component LONGVARBINARY,
    PRIMARY KEY (beer_id),
    FOREIGN KEY (beer_id) REFERENCES beer(id)
);

CREATE TABLE store_image (
	id BIGINT NOT NULL,
	store_name VARCHAR(255) NOT NULL UNIQUE,
    url VARCHAR(255) NOT NULL,
    remote_id VARCHAR(255),
    image_component LONGVARBINARY,
    PRIMARY KEY (id)
);

CREATE TABLE users (
    id BIGINT IDENTITY PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) CHECK (role in ('USER', 'ACCOUNTANT', 'ADMIN'))
);

CREATE SEQUENCE users_id_seq START WITH 4 INCREMENT BY 1;
CREATE SEQUENCE store_img_id_seq START WITH 4 INCREMENT BY 1;
CREATE SEQUENCE beer_seq START WITH 10 INCREMENT BY 1;
CREATE SEQUENCE store_seq START WITH 10 INCREMENT BY 1;