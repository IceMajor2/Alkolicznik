-- stores
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (1, 'Carrefour', 'Olsztyn', 'ul. Borkowskiego 13')
ON CONFLICT DO NOTHING;
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (2, 'Biedronka', 'Olsztyn', 'ul. Krasickiego 8')
ON CONFLICT DO NOTHING;
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (3, 'Grosik', 'Gdansk', 'ul. Marianskiego 9')
ON CONFLICT DO NOTHING;

-- beers
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (1, 'Perla', 'Chmielowa Pils', 0.5)
ON CONFLICT DO NOTHING;
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (2, 'Ksiazece', 'Zlote pszeniczne', 0.5)
ON CONFLICT DO NOTHING;
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (3, 'Tyskie', 'Gronie', 0.6)
ON CONFLICT DO NOTHING;
INSERT INTO beer (ID, BRAND, VOLUME) VALUES (4, 'Zubr', 0.5)
ON CONFLICT DO NOTHING;
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (5, 'Komes', 'Malinowe', 0.33)
ON CONFLICT DO NOTHING;
INSERT INTO beer (ID, BRAND, TYPE, VOLUME) VALUES (6, 'Miloslaw', 'Biale', 0.5)
ON CONFLICT DO NOTHING;

INSERT INTO image (BEER_ID, URL) VALUES (3, 'https://ik.imagekit.io/icemajor/test/tr:n-get_beer/tyskie-gronie.png')
ON CONFLICT DO NOTHING;
INSERT INTO image (BEER_ID, URL) VALUES (4, 'https://ik.imagekit.io/icemajor/test/tr:n-get_beer/zubr.png')
ON CONFLICT DO NOTHING;
INSERT INTO image (BEER_ID, URL) VALUES (5, 'https://ik.imagekit.io/icemajor/test/tr:n-get_beer/komes-porter-malinowy-0.33.png')
ON CONFLICT DO NOTHING;
INSERT INTO image (BEER_ID, URL) VALUES (6, 'https://ik.imagekit.io/icemajor/test/tr:n-get_beer/miloslaw-biale.png')
ON CONFLICT DO NOTHING;

-- store equipment
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (1, 1, 3.39, 'PLN')
ON CONFLICT DO NOTHING;
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (1, 3, 3.29, 'PLN')
ON CONFLICT DO NOTHING;
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (2, 4, 2.99, 'PLN')
ON CONFLICT DO NOTHING;
INSERT INTO beer_price (store_id, beer_id, price_amount, price_currency) VALUES (3, 6, 6.99, 'PLN')
ON CONFLICT DO NOTHING;

-- users
INSERT INTO users (ID, USERNAME, PASSWORD, ACCOUNT_NON_LOCKED, ROLES)
   VALUES (1, 'admin', '$2a$13$30DIvESARokaAl6jws6zz.7d6aH3QH7KW4SvVurPDU739U90UUiwu', true, ARRAY['ADMIN'])
       ON CONFLICT DO NOTHING;
INSERT INTO users (ID, USERNAME, PASSWORD, ACCOUNT_NON_LOCKED, ROLES)
   VALUES (2, 'user', '$2a$13$f/negBSxX5/nlzkFv/M7fOzS/b5AeuKYD/1ZqoES.zbPQiMGrL97y', true, ARRAY['USER'])
       ON CONFLICT DO NOTHING;
INSERT INTO users (ID, USERNAME, PASSWORD, ACCOUNT_NON_LOCKED, ROLES) -- 'user1', 'abcdef'
   VALUES (3, 'user1', '$2a$13$wiQL5II41f2vVVbnp1/5dOVkFNd9FuW6T/psofnp8p2YIVyLLu30y', true, ARRAY['ACCOUNTANT'])
       ON CONFLICT DO NOTHING;