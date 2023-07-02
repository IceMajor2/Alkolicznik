-- stores
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (nextval('store_id_seq'), 'Carrefour', 'Olsztyn', 'ul. Borkowskiego 13')
ON CONFLICT DO NOTHING;
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (nextval('store_id_seq'), 'Biedronka', 'Olsztyn', 'ul. Krasickiego 8')
ON CONFLICT DO NOTHING;
INSERT INTO store (ID, NAME, CITY, STREET) VALUES (nextval('store_id_seq'), 'Grosik', 'Gdansk', 'ul. Marianskiego 9')
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

-- store equipment
INSERT INTO beer_price (store_id, beer_id, price) VALUES (1, 1, 3.39)
ON CONFLICT DO NOTHING;
INSERT INTO beer_price (store_id, beer_id, price) VALUES (1, 3, 3.29)
ON CONFLICT DO NOTHING;
INSERT INTO beer_price (store_id, beer_id, price) VALUES (2, 4, 2.99)
ON CONFLICT DO NOTHING;
INSERT INTO beer_price (store_id, beer_id, price) VALUES (3, 6, 6.99)
ON CONFLICT DO NOTHING;