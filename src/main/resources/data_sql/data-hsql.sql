-- beers
INSERT INTO beer (ID, BRAND, TYPE) VALUES (1, 'Perla', 'Chmielowa Pils');
INSERT INTO beer (ID, BRAND, TYPE) VALUES (2, 'Ksiazece', 'Zlote pszeniczne');
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (3, 'Tyskie', 'Gronie', 0.65);
INSERT INTO beer (ID, BRAND) VALUES (4, 'Zubr');
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (5, 'Komes', 'Porter Malinowy', 0.33);
INSERT INTO beer (ID, BRAND, TYPE) VALUES (6, 'Miloslaw', 'Biale');
INSERT INTO beer (ID, BRAND) VALUES (7, 'Guinness');
INSERT INTO beer (ID, BRAND, TYPE) VALUES (8, 'Perla', 'Miodowa');
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (9, 'Perla', 'Chmielowa Pils', 0.33);

-- stores
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (1, 'Carrefour', 'Olsztyn', 'ul. Barcza 4');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (2, 'Biedronka', 'Olsztyn', 'ul. Sikorskiego-Wilczynskiego 12');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (3, 'Lidl', 'Olsztyn', 'ul. Iwaszkiewicza 1');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (4, 'ABC', 'Warszawa', 'ul. Zeromskiego 3');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (5, 'Lubi', 'Warszawa', 'ul. Nowaka 5');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (6, 'Grosik', 'Olsztyn', 'ul. Dworcowa 3');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (7, 'Tesco', 'Gdansk', 'ul. Morska 22');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (8, 'Carrefour', 'Olsztyn', 'ul. Borkowskiego 3');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (9, 'Zabka', 'Ilawa', 'ul. Dworcowa 3');

-- beer prices
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (1, 5, 5.39);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (1, 6, 5.49);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (1, 2, 5.19);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (1, 1, 3.99);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (2, 2, 4.99);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (2, 3, 4.19);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (2, 4, 2.89);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (3, 3, 4.19);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (3, 1, 3.89);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (4, 2, 5.49);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (4, 6, 5.99);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (4, 1, 4.09);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (5, 4, 3.09);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (5, 2, 5.49);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (5, 6, 6.09);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (5, 3, 4.59);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (6, 1, 4.99);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (6, 2, 6.19);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (6, 3, 5.09);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (6, 4, 4.19);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (6, 5, 7.49);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (6, 6, 7.09);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (2, 9, 2.99);

-- users
INSERT INTO users (ID, USERNAME, PASSWORD, ROLE) -- "admin", "admin"
    VALUES (1, 'admin', '$2a$13$30DIvESARokaAl6jws6zz.7d6aH3QH7KW4SvVurPDU739U90UUiwu', 'ADMIN');
INSERT INTO users (ID, USERNAME, PASSWORD, ROLE) -- "user", "user"
    VALUES (2, 'user', '$2a$13$0n3gSQKZ1747bFHIjfdAy./h.i6Buhas2uUAHAyXigrsn1n2IAMaC', 'USER');
INSERT INTO users (ID, USERNAME, PASSWORD, ROLE) -- "accountant", "accountant"
    VALUES (3, 'accountant', '$2a$13$5N9imk4bqMeSw1HJoxGgS.KIi.rbG0PxgnrJXmLaaNkNWbSowP5yK', 'ACCOUNTANT');