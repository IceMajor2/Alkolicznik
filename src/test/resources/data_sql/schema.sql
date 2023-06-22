DROP TABLE IF EXISTS beer_price;
DROP TABLE IF EXISTS store;
DROP TABLE IF EXISTS beer;

CREATE TABLE beer (
    id BIGINT IDENTITY PRIMARY KEY,
    brand VARCHAR(30) NOT NULL UNIQUE,
    type VARCHAR(30),
    volume FLOAT(53) DEFAULT 0.5);

CREATE TABLE store (
    id BIGINT IDENTITY PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE);

CREATE TABLE beer_price (
    beer_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    price FLOAT(53) NOT NULL,
    PRIMARY KEY (beer_id, store_id),
    FOREIGN KEY (beer_id) REFERENCES beer(id),
    FOREIGN KEY (store_id) REFERENCES store(id)
    );