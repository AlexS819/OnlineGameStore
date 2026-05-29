-- Migration V4: Add game details (description and image_url) and expand catalog to 20 games

-- 1. Alter games table
ALTER TABLE games ADD COLUMN description VARCHAR(2000);
ALTER TABLE games ADD COLUMN image_url VARCHAR(1000);

-- 2. Update existing games with realistic prices, descriptions, and image URLs
-- The Witcher 3: Wild Hunt (RPG)
-- AppID: 292030
UPDATE games SET 
    price = 29.99,
    description = 'The Witcher: Wild Hunt is a story-driven open world RPG set in a visually stunning fantasy universe full of meaningful choices and impactful consequences. You play as professional monster hunter Geralt of Rivia, tasked with finding a child of prophecy.',
    image_url = 'https://steamcdn-a.akamaihd.net/steam/apps/292030/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'
WHERE id = 'dd8d3796-d8f0-463e-a714-4ffcd79e319f';

-- Cyberpunk 2077 (RPG)
-- AppID: 1091500
UPDATE games SET 
    price = 59.99,
    description = 'Cyberpunk 2077 is an open-world, action-adventure RPG set in the megalopolis of Night City, where you play as a cyberpunk mercenary wrapped up in a do-or-die fight for survival. Customized character, cyberware and gameplay.',
    image_url = 'https://steamcdn-a.akamaihd.net/steam/apps/1091500/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'
WHERE id = '4a8cb3e3-e788-4749-b692-1b90b60c935f';

-- Assassin's Creed Valhalla (Action)
-- AppID: 2208920
UPDATE games SET 
    price = 49.99,
    description = 'Become Eivor, a legendary Viking warrior raised on tales of battle and glory. Explore England''s Dark Ages as you raid your enemies, grow your settlement, and build your political power in the quest to earn a place among the gods in Valhalla.',
    image_url = 'https://steamcdn-a.akamaihd.net/steam/apps/2208920/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'
WHERE id = '54ce37c9-6535-47e1-91ed-1243d15d944a';

-- FIFA 24 / EA Sports FC 24 (Simulation)
-- AppID: 2195250
UPDATE games SET 
    price = 69.99,
    description = 'EA SPORTS FC 24 welcomes you to The World''s Game: the most true-to-football experience ever with HyperMotionV, PlayStyles optimized by Opta, and a revolutionized Frostbite Engine.',
    image_url = 'https://steamcdn-a.akamaihd.net/steam/apps/2195250/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'
WHERE id = '1c5c6ea6-3854-4a77-b0dd-100c3bb65570';


-- 3. Insert 16 additional games with realistic prices, descriptions, and high-quality image URLs
INSERT INTO games (id, title, price, publisher_id, description, image_url) VALUES
-- RPGs (CDPR - e14c78d0-43d2-42bf-a2b8-0d610176f612)
('e00178d0-43d2-42bf-a2b8-0d610176f001', 'Elden Ring', 59.99, 'e14c78d0-43d2-42bf-a2b8-0d610176f612', 'Rise, Tarnished, and be guided by grace to brandish the power of the Elden Ring and become an Elden Lord in the Lands Between. A vast world where open fields with a variety of situations and giant dungeons are seamlessly connected.', 'https://steamcdn-a.akamaihd.net/steam/apps/1245620/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'),
('e00178d0-43d2-42bf-a2b8-0d610176f002', 'Skyrim Special Edition', 39.99, 'e14c78d0-43d2-42bf-a2b8-0d610176f612', 'Winner of more than 200 Game of the Year Awards, Skyrim Special Edition brings the epic fantasy to life in stunning detail. The Special Edition includes the critically acclaimed game and add-ons with all-new features.', 'https://steamcdn-a.akamaihd.net/steam/apps/489830/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'),
('e00178d0-43d2-42bf-a2b8-0d610176f003', 'Baldur''s Gate 3', 59.99, 'e14c78d0-43d2-42bf-a2b8-0d610176f612', 'Gather your party and return to the Forgotten Realms in a tale of fellowship and betrayal, sacrifice and survival, and the lure of absolute power. Mysterious abilities are awakening within you, drawn from a mind flayer parasite.', 'https://steamcdn-a.akamaihd.net/steam/apps/1086940/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'),

-- Actions (Ubisoft - c23e6f7d-1c44-490f-834f-32c2128efab1)
('c0026f7d-1c44-490f-834f-32c2128ef001', 'Far Cry 6', 59.99, 'c23e6f7d-1c44-490f-834f-32c2128efab1', 'Welcome to Yara, a tropical paradise frozen in time. As the dictator of Yara, Anton Castillo is intent on restoring his nation back to its former glory by any means, with his son, Diego, following in his bloody footsteps.', 'https://steamcdn-a.akamaihd.net/steam/apps/2369390/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'),
('c0026f7d-1c44-490f-834f-32c2128ef002', 'Tom Clancy''s Rainbow Six Siege', 19.99, 'c23e6f7d-1c44-490f-834f-32c2128efab1', 'Rainbow Six Siege is an elite, tactical team-based shooter where superior planning and execution triumph. Features 5v5 attack vs. defense gameplay and intense close-quarters combat in destructible environments.', 'https://steamcdn-a.akamaihd.net/steam/apps/359550/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'),
('c0026f7d-1c44-490f-834f-32c2128ef003', 'Assassin''s Creed Odyssey', 59.99, 'c23e6f7d-1c44-490f-834f-32c2128efab1', 'Write your own legendary odyssey and live an epic adventure in a world where every choice matters. Sentenced to death by your family, embark on an epic journey from outcast mercenary to legendary Spartan hero.', 'https://steamcdn-a.akamaihd.net/steam/apps/812140/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'),
('c0026f7d-1c44-490f-834f-32c2128ef004', 'Watch Dogs: Legion', 59.99, 'c23e6f7d-1c44-490f-834f-32c2128efab1', 'Build a resistance from virtually anyone you see as you hack, infiltrate, and fight to take back a near-future London that is facing its downfall. Welcome to the Resistance.', 'https://steamcdn-a.akamaihd.net/steam/apps/2239550/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'),

-- Simulations & Sports (Electronic Arts - d97c5a8b-4b2b-4a06-90a0-9f0d3c8a9e10)
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9001', 'The Sims 4', 19.99, 'd97c5a8b-4b2b-4a06-90a0-9f0d3c8a9e10', 'Play with life and discover the possibilities. Unleash your imagination and create a world of Sims that''s wholly unique. Customize every detail of your Sims, their homes, and much more.', 'https://steamcdn-a.akamaihd.net/steam/apps/1222670/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'),
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9002', 'Need for Speed Unbound', 69.99, 'd97c5a8b-4b2b-4a06-90a0-9f0d3c8a9e10', 'Race against time, outsmart the cops, and take on weekly qualifiers to reach The Grand, Lakeshore''s ultimate street racing challenge. Pack your garage with precision-tuned, custom rides.', 'https://steamcdn-a.akamaihd.net/steam/apps/1846380/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'),
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9003', 'Battlefield 2042', 59.99, 'd97c5a8b-4b2b-4a06-90a0-9f0d3c8a9e10', 'Battlefield 2042 is a first-person shooter that marks the return to the iconic all-out warfare of the franchise. Adapt and overcome in a near-future world transformed by disorder.', 'https://steamcdn-a.akamaihd.net/steam/apps/1517290/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'),
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9004', 'F1 23', 69.99, 'd97c5a8b-4b2b-4a06-90a0-9f0d3c8a9e10', 'Be the last to brake in EA SPORTS F1 23, the official videogame of the 2023 FIA Formula One World Championship. A new chapter in the thrilling "Braking Point" story mode delivers high-speed drama.', 'https://steamcdn-a.akamaihd.net/steam/apps/2108330/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'),
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9005', 'It Takes Two', 39.99, 'd97c5a8b-4b2b-4a06-90a0-9f0d3c8a9e10', 'Embark on the craziest journey of your life in It Takes Two, a genre-bending platform adventure created purely for co-op. Invite a friend to join for free with Friend’s Pass and work together.', 'https://steamcdn-a.akamaihd.net/steam/apps/1426210/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'),
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9006', 'Star Wars Jedi: Survivor', 69.99, 'd97c5a8b-4b2b-4a06-90a0-9f0d3c8a9e10', 'The story of Cal Kestis continues in Star Wars Jedi: Survivor, a third-person, galaxy-spanning, action-adventure game from Respawn Entertainment, developed in collaboration with Lucasfilm Games.', 'https://steamcdn-a.akamaihd.net/steam/apps/1774580/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'),
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9007', 'Dead Space Remake', 59.99, 'd97c5a8b-4b2b-4a06-90a0-9f0d3c8a9e10', 'The sci-fi survival-horror classic Dead Space returns, completely rebuilt from the ground up to offer a deeper, more immersive experience - preserving the original game''s thrilling vision.', 'https://steamcdn-a.akamaihd.net/steam/apps/1693980/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'),
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9008', 'Mass Effect Legendary Edition', 59.99, 'd97c5a8b-4b2b-4a06-90a0-9f0d3c8a9e10', 'One person is all that stands between humanity and the greatest threat it has ever faced. Relive the legend of Commander Shepard in the highly acclaimed Mass Effect trilogy.', 'https://steamcdn-a.akamaihd.net/steam/apps/1328670/library_600x900.jpg?w=600&auto=format&fit=crop&q=80'),
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9009', 'Apex Legends Champions Edition', 39.99, 'd97c5a8b-4b2b-4a06-90a0-9f0d3c8a9e10', 'Conquer with character in Apex Legends, a free-to-play Hero shooter where legendary characters with powerful abilities team up to battle for fame & fortune on the fringes of the Frontier.', 'https://steamcdn-a.akamaihd.net/steam/apps/1172470/library_600x900.jpg?w=600&auto=format&fit=crop&q=80');

-- 4. Associate the new games with appropriate Genres
INSERT INTO game_genres (game_id, genre_id) VALUES
-- Elden Ring -> RPG, Action
('e00178d0-43d2-42bf-a2b8-0d610176f001', '03f9dd50-b832-4a40-823a-93c163ec3ced'),
('e00178d0-43d2-42bf-a2b8-0d610176f001', '9844d378-ac63-4275-a963-388d4948ad21'),
-- Skyrim -> RPG, Adventure
('e00178d0-43d2-42bf-a2b8-0d610176f002', '03f9dd50-b832-4a40-823a-93c163ec3ced'),
('e00178d0-43d2-42bf-a2b8-0d610176f002', '31bf54c7-00fb-4ef4-8b66-2bf8110304b7'),
-- Baldur's Gate 3 -> RPG, Strategy
('e00178d0-43d2-42bf-a2b8-0d610176f003', '03f9dd50-b832-4a40-823a-93c163ec3ced'),
('e00178d0-43d2-42bf-a2b8-0d610176f003', '4ae030a0-d9b9-47b2-9e11-413bb7de23be'),
-- Far Cry 6 -> Action, Adventure
('c0026f7d-1c44-490f-834f-32c2128ef001', '9844d378-ac63-4275-a963-388d4948ad21'),
('c0026f7d-1c44-490f-834f-32c2128ef001', '31bf54c7-00fb-4ef4-8b66-2bf8110304b7'),
-- Rainbow Six Siege -> Action, Strategy
('c0026f7d-1c44-490f-834f-32c2128ef002', '9844d378-ac63-4275-a963-388d4948ad21'),
('c0026f7d-1c44-490f-834f-32c2128ef002', '4ae030a0-d9b9-47b2-9e11-413bb7de23be'),
-- AC Odyssey -> Action, RPG
('c0026f7d-1c44-490f-834f-32c2128ef003', '9844d378-ac63-4275-a963-388d4948ad21'),
('c0026f7d-1c44-490f-834f-32c2128ef003', '03f9dd50-b832-4a40-823a-93c163ec3ced'),
-- Watch Dogs -> Action, Adventure
('c0026f7d-1c44-490f-834f-32c2128ef004', '9844d378-ac63-4275-a963-388d4948ad21'),
('c0026f7d-1c44-490f-834f-32c2128ef004', '31bf54c7-00fb-4ef4-8b66-2bf8110304b7'),
-- Sims 4 -> Simulation
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9001', 'a154aca6-ffdb-42cc-912d-eb20382819b2'),
-- NFS Unbound -> Simulation, Action
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9002', 'a154aca6-ffdb-42cc-912d-eb20382819b2'),
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9002', '9844d378-ac63-4275-a963-388d4948ad21'),
-- Battlefield 2042 -> Action
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9003', '9844d378-ac63-4275-a963-388d4948ad21'),
-- F1 23 -> Simulation
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9004', 'a154aca6-ffdb-42cc-912d-eb20382819b2'),
-- It Takes Two -> Adventure, Action
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9005', '31bf54c7-00fb-4ef4-8b66-2bf8110304b7'),
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9005', '9844d378-ac63-4275-a963-388d4948ad21'),
-- Jedi Survivor -> Adventure, Action
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9006', '31bf54c7-00fb-4ef4-8b66-2bf8110304b7'),
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9006', '9844d378-ac63-4275-a963-388d4948ad21'),
-- Dead Space -> Action, RPG
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9007', '9844d378-ac63-4275-a963-388d4948ad21'),
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9007', '03f9dd50-b832-4a40-823a-93c163ec3ced'),
-- Mass Effect -> RPG, Action
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9008', '03f9dd50-b832-4a40-823a-93c163ec3ced'),
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9008', '9844d378-ac63-4275-a963-388d4948ad21'),
-- Apex Legends -> Action, Simulation
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9009', '9844d378-ac63-4275-a963-388d4948ad21'),
('d0035a8b-4b2b-4a06-90a0-9f0d3c8a9009', 'a154aca6-ffdb-42cc-912d-eb20382819b2');

-- 5. Seed stock activation keys for all 20 games (2 AVAILABLE keys for each game)
INSERT INTO activation_keys (id, key_value, status, game_id, order_id) VALUES
-- Witcher 3 (dd8d3796-d8f0-463e-a714-4ffcd79e319f) key already has available key? Yes, in V2, we add more:
('a001b1b2-f97e-4510-aaf6-843d85751001', 'W3AAA-K1Y11-SLKEE-88888', 'AVAILABLE', 'dd8d3796-d8f0-463e-a714-4ffcd79e319f', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751002', 'W3BBB-K1Y22-SLKEE-99999', 'AVAILABLE', 'dd8d3796-d8f0-463e-a714-4ffcd79e319f', NULL),
-- Cyberpunk (4a8cb3e3-e788-4749-b692-1b90b60c935f) has keys in V2, but let's add one more
('a001b1b2-f97e-4510-aaf6-843d85751003', 'CP777-KEY33-CYBER-99999', 'AVAILABLE', '4a8cb3e3-e788-4749-b692-1b90b60c935f', NULL),
-- Assassin's Creed Valhalla (54ce37c9-6535-47e1-91ed-1243d15d944a)
('a001b1b2-f97e-4510-aaf6-843d85751004', 'ACV11-VALHA-LLA22-11111', 'AVAILABLE', '54ce37c9-6535-47e1-91ed-1243d15d944a', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751005', 'ACV22-VALHA-LLA33-22222', 'AVAILABLE', '54ce37c9-6535-47e1-91ed-1243d15d944a', NULL),
-- FIFA 24 (1c5c6ea6-3854-4a77-b0dd-100c3bb65570)
('a001b1b2-f97e-4510-aaf6-843d85751006', 'FC241-FOOTB-ALL22-33333', 'AVAILABLE', '1c5c6ea6-3854-4a77-b0dd-100c3bb65570', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751007', 'FC242-FOOTB-ALL33-44444', 'AVAILABLE', '1c5c6ea6-3854-4a77-b0dd-100c3bb65570', NULL),
-- Elden Ring
('a001b1b2-f97e-4510-aaf6-843d85751008', 'ER111-RINGG-ELDEN-11111', 'AVAILABLE', 'e00178d0-43d2-42bf-a2b8-0d610176f001', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751009', 'ER222-RINGG-ELDEN-22222', 'AVAILABLE', 'e00178d0-43d2-42bf-a2b8-0d610176f001', NULL),
-- Skyrim
('a001b1b2-f97e-4510-aaf6-843d85751010', 'SKY11-DRAGO-NBORN-11111', 'AVAILABLE', 'e00178d0-43d2-42bf-a2b8-0d610176f002', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751011', 'SKY22-DRAGO-NBORN-22222', 'AVAILABLE', 'e00178d0-43d2-42bf-a2b8-0d610176f002', NULL),
-- Baldur's Gate 3
('a001b1b2-f97e-4510-aaf6-843d85751012', 'BG333-GATEE-BALDU-33333', 'AVAILABLE', 'e00178d0-43d2-42bf-a2b8-0d610176f003', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751013', 'BG444-GATEE-BALDU-44444', 'AVAILABLE', 'e00178d0-43d2-42bf-a2b8-0d610176f003', NULL),
-- Far Cry 6
('a001b1b2-f97e-4510-aaf6-843d85751014', 'FC611-YARAA-LIBER-11111', 'AVAILABLE', 'c0026f7d-1c44-490f-834f-32c2128ef001', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751015', 'FC622-YARAA-LIBER-22222', 'AVAILABLE', 'c0026f7d-1c44-490f-834f-32c2128ef001', NULL),
-- Rainbow Six Siege
('a001b1b2-f97e-4510-aaf6-843d85751016', 'R6S11-SIEGE-TACTI-11111', 'AVAILABLE', 'c0026f7d-1c44-490f-834f-32c2128ef002', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751017', 'R6S22-SIEGE-TACTI-22222', 'AVAILABLE', 'c0026f7d-1c44-490f-834f-32c2128ef002', NULL),
-- AC Odyssey
('a001b1b2-f97e-4510-aaf6-843d85751018', 'ACO11-SPART-GREEK-11111', 'AVAILABLE', 'c0026f7d-1c44-490f-834f-32c2128ef003', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751019', 'ACO22-SPART-GREEK-22222', 'AVAILABLE', 'c0026f7d-1c44-490f-834f-32c2128ef003', NULL),
-- Watch Dogs
('a001b1b2-f97e-4510-aaf6-843d85751020', 'WDL11-DEDSE-CLOND-11111', 'AVAILABLE', 'c0026f7d-1c44-490f-834f-32c2128ef004', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751021', 'WDL22-DEDSE-CLOND-22222', 'AVAILABLE', 'c0026f7d-1c44-490f-834f-32c2128ef004', NULL),
-- Sims 4
('a001b1b2-f97e-4510-aaf6-843d85751022', 'SIM11-LIFEE-CREAT-11111', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9001', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751023', 'SIM22-LIFEE-CREAT-22222', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9001', NULL),
-- NFS Unbound
('a001b1b2-f97e-4510-aaf6-843d85751024', 'NFS11-SPEED-RACEE-11111', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9002', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751025', 'NFS22-SPEED-RACEE-22222', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9002', NULL),
-- Battlefield 2042
('a001b1b2-f97e-4510-aaf6-843d85751026', 'BF222-WARFA-RE422-11111', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9003', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751027', 'BF333-WARFA-RE422-22222', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9003', NULL),
-- F1 23
('a001b1b2-f97e-4510-aaf6-843d85751028', 'F1231-RACEE-FORMA-11111', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9004', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751029', 'F1232-RACEE-FORMA-22222', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9004', NULL),
-- It Takes Two
('a001b1b2-f97e-4510-aaf6-843d85751030', 'ITT11-COOPE-RATIV-11111', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9005', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751031', 'ITT22-COOPE-RATIV-22222', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9005', NULL),
-- Jedi Survivor
('a001b1b2-f97e-4510-aaf6-843d85751032', 'JED11-CALKE-STISS-11111', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9006', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751033', 'JED22-CALKE-STISS-22222', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9006', NULL),
-- Dead Space
('a001b1b2-f97e-4510-aaf6-843d85751034', 'DS111-SPACE-HORRO-11111', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9007', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751035', 'DS222-SPACE-HORRO-22222', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9007', NULL),
-- Mass Effect
('a001b1b2-f97e-4510-aaf6-843d85751036', 'ME111-SHEPA-RDLEG-11111', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9008', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751037', 'ME222-SHEPA-RDLEG-22222', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9008', NULL),
-- Apex Legends
('a001b1b2-f97e-4510-aaf6-843d85751038', 'APX11-CHAMP-LEGND-11111', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9009', NULL),
('a001b1b2-f97e-4510-aaf6-843d85751039', 'APX22-CHAMP-LEGND-22222', 'AVAILABLE', 'd0035a8b-4b2b-4a06-90a0-9f0d3c8a9009', NULL);
