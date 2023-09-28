-- stores
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (1, 'Biedronka', 'Poznan', 'ul. Libelta 5');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (2, 'Spolem', 'Olsztyn', 'ul. Wilczynskiego 8');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (3, 'Kaufland', 'Warszawa', 'ul. Batalionow Chlopskich 73');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (4, 'Lewiatan', 'Bialystok', 'ul. Jozefa Ignacego Kraszewskiego 17/1');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (5, 'Lidl', 'Olsztyn', 'ul. Kolobrzeska 31');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (6, 'Blawatek', 'Olsztyn', 'ul. Baltycka 35');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (7, 'Lidl', 'Bialystok', 'ul. Jesionowa 1a');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (8, 'Market Kami', 'Gdynia', 'ul. Narcyzowa 1/13A');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (9, 'Stokrotka', 'Szczecin', 'al. Niepodleglosci 18/22');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (10, 'Biedronka', 'Wroclaw', 'ul. Zelazna 54/56');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (11, 'Lewiatan', 'Katowice', 'ul. Powstancow 19');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (12, 'Kaufland', 'Lodz', 'ul. Walerego Wroblewskiego 68');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (13, 'Biedronka', 'Szczecin', 'ul. Lukasinskiego 116');
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (14, 'M&P', 'Lodz', 'ul. Zgierska 211/1B');

-- beers
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (1, 'Perla', 'Chmielowa Pils', 0.33);
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (2, 'Ksiazece', 'Zlote pszeniczne', 0.5);
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (3, 'Pilsner', 'Urquell', 0.5);
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (4, 'Okocim', 'Mocne Dubeltowe', 0.5);
INSERT INTO beer (ID, BRAND, VOLUME) VALUES (5, 'Warka', 0.5);
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (6, 'Kormoran', 'Swieze', 0.5);
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (7, 'Manufaktura Piwna', 'Piwo na miodzie gryczanym', 0.5);
INSERT INTO beer (ID, BRAND, VOLUME) VALUES (8, 'Zubr', 0.5);
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (9, 'Guinness', 'Draught', 0.33);
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (10, 'Tatra', 'Jasne Pelne', 0.5);

-- store equipment
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (1, 1, 2.99);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (1, 3, 4.99);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (1, 4, 6.29);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (2, 1, 2.59);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (2, 2, 5.99);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (2, 7, 5.99);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (3, 8, 3.79);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (3, 3, 5.99);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (4, 10, 3.29);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (4, 8, 2.99);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (4, 2, 5.09);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (5, 8, 3.09);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (5, 5, 3.49);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (5, 6, 6.99);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (6, 6, 6.89);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (6, 1, 2.89);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (6, 5, 3.29);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (7, 8, 2.89);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (7, 10, 3.89);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (8, 1, 3.19);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (8, 4, 5.89);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (9, 10, 3.59);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (9, 2, 5.19);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (9, 4, 5.69);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (10, 3, 6.39);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (10, 5, 3.69);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (10, 7, 5.89);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (11, 1, 3.09);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (11, 8, 3.89);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (11, 10, 4.09);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (12, 3, 4.89);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (12, 2, 4.99);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (12, 8, 3.09);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (13, 10, 2.99);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (13, 2, 6.09);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (13, 3, 5.99);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (14, 1, 2.89);
INSERT INTO beer_price (store_id, beer_id, price_amount) VALUES (14, 7, 5.39);

-- users
INSERT INTO users (ID, USERNAME, PASSWORD, ROLE) -- "admin", "admin"
    VALUES (1, 'admin', '$2a$13$30DIvESARokaAl6jws6zz.7d6aH3QH7KW4SvVurPDU739U90UUiwu', 'ADMIN');
INSERT INTO users (ID, USERNAME, PASSWORD, ROLE) -- "user", "user"
    VALUES (2, 'user', '$2a$13$0n3gSQKZ1747bFHIjfdAy./h.i6Buhas2uUAHAyXigrsn1n2IAMaC', 'USER');
INSERT INTO users (ID, USERNAME, PASSWORD, ROLE) -- "accountant", "accountant"
    VALUES (3, 'accountant', '$2a$13$5N9imk4bqMeSw1HJoxGgS.KIi.rbG0PxgnrJXmLaaNkNWbSowP5yK', 'ACCOUNTANT');