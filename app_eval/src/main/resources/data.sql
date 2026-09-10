-- Seed H2 de demonstration. Dates fixes pour des tests reproductibles.
INSERT INTO users (id, first_name, last_name, email, password_hash, role, created_at, updated_at) VALUES
 (1, 'Marie', 'Dupont', 'marie.dupont@shopwise.test', '$2a$10$demoHashForMarie', 'ADMIN', TIMESTAMP '2026-01-10 09:00:00', TIMESTAMP '2026-01-10 09:00:00'),
 (2, 'Lucas', 'Martin', 'lucas.martin@shopwise.test', '$2a$10$demoHashForLucas', 'USER', TIMESTAMP '2026-01-11 10:00:00', TIMESTAMP '2026-01-11 10:00:00');
INSERT INTO categories (id, name, created_at, updated_at) VALUES
 (1, 'Epicerie', TIMESTAMP '2026-01-12 09:00:00', TIMESTAMP '2026-01-12 09:00:00'),
 (2, 'Boissons', TIMESTAMP '2026-01-12 09:05:00', TIMESTAMP '2026-01-12 09:05:00'),
 (3, 'Hygiene', TIMESTAMP '2026-01-12 09:10:00', TIMESTAMP '2026-01-12 09:10:00');
INSERT INTO products (id, sku, name, description, price, created_at, updated_at) VALUES
 (1, 'CAFE-ARABICA-250', 'Cafe Arabica 250g', 'Cafe moulu issu du commerce equitable', 6.90, TIMESTAMP '2026-01-13 08:00:00', TIMESTAMP '2026-01-13 08:00:00'),
 (2, 'THE-VERT-100', 'The vert 100g', 'The vert nature en vrac', 4.50, TIMESTAMP '2026-01-13 08:05:00', TIMESTAMP '2026-01-13 08:05:00'),
 (3, 'EAU-PLATE-150', 'Eau minerale 1.5L', 'Bouteille d eau minerale', 0.80, TIMESTAMP '2026-01-13 08:10:00', TIMESTAMP '2026-01-13 08:10:00'),
 (4, 'SAVON-DOUX-100', 'Savon doux 100g', 'Savon solide pour les mains', 2.40, TIMESTAMP '2026-01-13 08:15:00', TIMESTAMP '2026-01-13 08:15:00');
INSERT INTO product_categories (product_id, category_id) VALUES (1, 1), (1, 2), (2, 2), (3, 2), (4, 3);
INSERT INTO sales (id, user_id, total_price, created_at, updated_at) VALUES
 (1, 1, 15.40, TIMESTAMP '2026-01-14 11:20:00', TIMESTAMP '2026-01-14 11:20:00'),
 (2, 2, 7.70, TIMESTAMP '2026-01-15 16:45:00', TIMESTAMP '2026-01-15 16:45:00');
INSERT INTO sale_items (id, sale_id, product_id, quantity, unit_price) VALUES
 (1, 1, 1, 2, 6.90), (2, 1, 3, 2, 0.80), (3, 2, 2, 1, 4.50), (4, 2, 4, 1, 2.40), (5, 2, 3, 1, 0.80);
ALTER TABLE users ALTER COLUMN id RESTART WITH 3;
ALTER TABLE categories ALTER COLUMN id RESTART WITH 4;
ALTER TABLE products ALTER COLUMN id RESTART WITH 5;
ALTER TABLE sales ALTER COLUMN id RESTART WITH 3;
ALTER TABLE sale_items ALTER COLUMN id RESTART WITH 6;
