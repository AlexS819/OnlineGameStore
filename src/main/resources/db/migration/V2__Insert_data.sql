-- System Data Seeding Protocol

-- 1. Insert Publishers
INSERT INTO publishers (id, name, website, support_email) VALUES
('d97c5a8b-4b2b-4a06-90a0-9f0d3c8a9e10', 'Electronic Arts', 'https://ea.com', 'support@ea.com'),
('c23e6f7d-1c44-490f-834f-32c2128efab1', 'Ubisoft', 'https://ubisoft.com', 'help@ubisoft.com'),
('e14c78d0-43d2-42bf-a2b8-0d610176f612', 'CD Projekt Red', 'https://cdprojektred.com', 'support@cdpr.com');

-- 2. Insert Initial Users
INSERT INTO users (id, name, email, password_hash, role) VALUES
('646dc04d-6a91-4ffe-9a9e-64523e9e591b', 'Admin Zhuma', 'admin@gamestore.com', '$2a$12$jPIWNSUarC4PP5dRL.EH5OZq/zxmLqGhyjc7iYG1F34ICRYGXmf6O', 'ADMIN'),
('446b700b-7fd7-44c3-89b9-2659c39ae9cf', 'Petro Ivanov', 'petro@mail.com', '$2a$12$XTzWmcxuwSTS6IzULDVnB.4Mil9HCm.GYKeM1RSbbB6oZCGrRM7Ue', 'USER');

-- 3. Insert Catalog Genres
INSERT INTO genres (id, name) VALUES
('03f9dd50-b832-4a40-823a-93c163ec3ced', 'RPG'),
('9844d378-ac63-4275-a963-388d4948ad21', 'Action'),
('31bf54c7-00fb-4ef4-8b66-2bf8110304b7', 'Adventure'),
('a154aca6-ffdb-42cc-912d-eb20382819b2', 'Simulation'),
('4ae030a0-d9b9-47b2-9e11-413bb7de23be', 'Strategy');

-- 4. Insert Catalog Games
INSERT INTO games (id, title, price, publisher_id) VALUES
('dd8d3796-d8f0-463e-a714-4ffcd79e319f', 'The Witcher 3', 599.99, 'e14c78d0-43d2-42bf-a2b8-0d610176f612'),
('4a8cb3e3-e788-4749-b692-1b90b60c935f', 'Cyberpunk 2077', 899.50, 'e14c78d0-43d2-42bf-a2b8-0d610176f612'),
('54ce37c9-6535-47e1-91ed-1243d15d944a', 'Assassin Creed Valhalla', 1200.00, 'c23e6f7d-1c44-490f-834f-32c2128efab1'),
('1c5c6ea6-3854-4a77-b0dd-100c3bb65570', 'FIFA 24', 1500.00, 'd97c5a8b-4b2b-4a06-90a0-9f0d3c8a9e10');

-- 5. Associate Many-to-Many Relationships
INSERT INTO game_genres (game_id, genre_id) VALUES
('dd8d3796-d8f0-463e-a714-4ffcd79e319f', '03f9dd50-b832-4a40-823a-93c163ec3ced'),
('dd8d3796-d8f0-463e-a714-4ffcd79e319f', '9844d378-ac63-4275-a963-388d4948ad21'),
('dd8d3796-d8f0-463e-a714-4ffcd79e319f', '31bf54c7-00fb-4ef4-8b66-2bf8110304b7'),
('4a8cb3e3-e788-4749-b692-1b90b60c935f', '03f9dd50-b832-4a40-823a-93c163ec3ced'),
('4a8cb3e3-e788-4749-b692-1b90b60c935f', '9844d378-ac63-4275-a963-388d4948ad21');

-- 6. Seed Historical Orders
INSERT INTO orders (id, user_id, created_at, total_price) VALUES
('4a86f112-48f9-4ce9-b567-10a31b1d8d17', '446b700b-7fd7-44c3-89b9-2659c39ae9cf', NOW(), 599.99);

-- 7. Stock Activation Keys
INSERT INTO activation_keys (id, key_value, status, game_id, order_id) VALUES
('fd08b1b2-f97e-4510-aaf6-843d85751d92', 'GTY88-VBN44-KLOP9-10001', 'SOLD', 'dd8d3796-d8f0-463e-a714-4ffcd79e319f', '4a86f112-48f9-4ce9-b567-10a31b1d8d17'),
('947f116a-282b-4389-9ad7-b706077076d4', 'X2J4K-99G7V-PLQZM-88888', 'AVAILABLE', '4a8cb3e3-e788-4749-b692-1b90b60c935f', NULL),
('7dd2728f-1bf4-4953-8bab-d5cbffaba5f7', 'QWE44-RTY55-UIOP6-77777', 'AVAILABLE', '4a8cb3e3-e788-4749-b692-1b90b60c935f', NULL);
