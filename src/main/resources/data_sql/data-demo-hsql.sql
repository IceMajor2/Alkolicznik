-- stores
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (1, 'Carrefour', 'Olsztyn', 'ul. Borkowskiego 13');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (2, 'Biedronka', 'Olsztyn', 'ul. Krasickiego 8');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (3, 'Grosik', 'Gdansk', 'ul. Marianskiego 9');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (4, 'Dwojka', 'Warszawa', 'al. Wojska Polskiego 123');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (5, 'Biedronka', 'Warszawa', 'ul. Nieglowicza 15');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (6, 'Carrefour', 'Wroclaw', 'al. Hallera 52');

-- beers
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (1, 'Perla', 'Chmielowa Pils', 0.5);
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (2, 'Ksiazece', 'Zlote pszeniczne', 0.5);
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (3, 'Tyskie', 'Gronie', 0.6);
INSERT INTO beer (ID, BRAND, VOLUME) VALUES (4, 'Zubr', 0.5);
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (5, 'Komes', 'Malinowe', 0.33);
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (6, 'Miloslaw', 'Biale', 0.5);
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (7, 'Manufaktura Piwna', 'Piwo na miodzie gryczanym', 0.5);
INSERT INTO beer (ID, BRAND, VOLUME) VALUES (8, 'Namyslow', 0.5);
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (9, 'Perla', 'Miodowa', 0.33);
INSERT INTO beer (ID, BRAND, VOLUME) VALUES (10, 'Lomza', 0.5);

-- store equipment
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (1, 1, 3.39, 'PLN');
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (1, 3, 3.29, 'PLN');
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (1, 4, 2.99, 'PLN');
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (2, 1, 6.99, 'PLN');
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (2, 2, 5.99, 'PLN');
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (2, 7, 5.99, 'PLN');
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (2, 9, 4.09, 'PLN');
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (3, 8, 3.89, 'PLN');
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (3, 3, 5.99, 'PLN');
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (4, 10, 3.89, 'PLN');
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (4, 6, 6.99, 'PLN');
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (4, 7, 6.49, 'PLN');
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (5, 1, 3.99, 'PLN');
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (5, 4, 3.29, 'PLN');
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (5, 7, 6.19, 'PLN');
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (6, 10, 3.89, 'PLN');

-- users
INSERT INTO users (ID, USERNAME, PASSWORD, ROLE) -- "admin", "admin"
    VALUES (1, 'admin', '$2a$13$30DIvESARokaAl6jws6zz.7d6aH3QH7KW4SvVurPDU739U90UUiwu', 'ADMIN');
INSERT INTO users (ID, USERNAME, PASSWORD, ROLE) -- "user", "user"
    VALUES (2, 'user', '$2a$13$0n3gSQKZ1747bFHIjfdAy./h.i6Buhas2uUAHAyXigrsn1n2IAMaC', 'USER');
INSERT INTO users (ID, USERNAME, PASSWORD, ROLE) -- "accountant", "accountant"
    VALUES (3, 'accountant', '$2a$13$5N9imk4bqMeSw1HJoxGgS.KIi.rbG0PxgnrJXmLaaNkNWbSowP5yK', 'ACCOUNTANT');