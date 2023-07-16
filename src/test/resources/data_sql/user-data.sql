INSERT INTO users (ID, USERNAME, PASSWORD, ACCOUNT_NON_LOCKED, ROLES) -- "admin", "admin"
    VALUES (1, 'admin', '$2a$13$30DIvESARokaAl6jws6zz.7d6aH3QH7KW4SvVurPDU739U90UUiwu', true, ARRAY['ADMIN']);
INSERT INTO users (ID, USERNAME, PASSWORD, ACCOUNT_NON_LOCKED, ROLES) -- "jacek", "Lasuviyojo90"
    VALUES (2, 'jacek', '$2y$13$gs0zdMMq/Ed1PFEiOWi7ZeJaFlXryv6bJ6ywgkmf1yQMh3DFeVfpm', true, ARRAY['USER']);
INSERT INTO users (ID, USERNAME, PASSWORD, ACCOUNT_NON_LOCKED, ROLES) -- "user", "user"
    VALUES (3, 'user', '$2a$13$0n3gSQKZ1747bFHIjfdAy./h.i6Buhas2uUAHAyXigrsn1n2IAMaC', true, ARRAY['USER']);
INSERT INTO users (ID, USERNAME, PASSWORD, ACCOUNT_NON_LOCKED, ROLES) -- "kacprox07", "Kodobarazi20"
    VALUES (4, 'kacprox07', '$2y$13$8gz4b5kwgWQ2t6a.SY3lk.Uct6zySI1blvdh6MF.8EmEMDd1ufulu', true, ARRAY['ACCOUNTANT']);
INSERT INTO users (ID, USERNAME, PASSWORD, ACCOUNT_NON_LOCKED, ROLES) -- "heckler", "Gaziwubalu66"
    VALUES (5, 'heckler', '$2y$13$z951BviP1jqU7jfpJ7j8g.jISs7hOcZxJH/dTqzy/NSfaGcT0/vNu', false, ARRAY['USER']);
INSERT INTO users (ID, USERNAME, PASSWORD, ACCOUNT_NON_LOCKED, ROLES) -- "accountant", "accountant"
    VALUES (6, 'accountant', '$2a$13$5N9imk4bqMeSw1HJoxGgS.KIi.rbG0PxgnrJXmLaaNkNWbSowP5yK', true, ARRAY['ACCOUNTANT']);