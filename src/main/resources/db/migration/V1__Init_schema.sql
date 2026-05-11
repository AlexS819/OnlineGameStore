-- Digital Goods Catalog & Key Accounting Subsystem
-- Initial Schema Setup

DROP TABLE IF EXISTS game_genres;
DROP TABLE IF EXISTS activation_keys;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS games;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS publishers;

-- -----------------------------------------------------
-- Table: publishers
-- Description: Stores company profile data for game distributors.
-- Normal Form: 3NF
-- -----------------------------------------------------
CREATE TABLE publishers (
    id CHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    website VARCHAR(255),
    support_email VARCHAR(255),
    CONSTRAINT pk_publishers PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- Table: users
-- Description: Stores registered platform users and their roles.
-- Normal Form: 3NF
-- -----------------------------------------------------
CREATE TABLE users (
    id CHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- Table: genres
-- Description: Store categorization dictionary for the catalog.
-- Normal Form: 3NF
-- -----------------------------------------------------
CREATE TABLE genres (
    id CHAR(36) NOT NULL,
    name VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT pk_genres PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- Table: games
-- Description: Digital product entity repository.
-- Normal Form: 3NF (Depends on publisher through FK)
-- -----------------------------------------------------
CREATE TABLE games (
    id CHAR(36) NOT NULL,
    title VARCHAR(200) NOT NULL,
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    publisher_id CHAR(36) NOT NULL,
    CONSTRAINT pk_games PRIMARY KEY (id),
    CONSTRAINT fk_games_publishers FOREIGN KEY (publisher_id) REFERENCES publishers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- Table: game_genres
-- Description: Junction table for Many-to-Many game categorization.
-- Normal Form: 3NF
-- -----------------------------------------------------
CREATE TABLE game_genres (
    game_id CHAR(36) NOT NULL,
    genre_id CHAR(36) NOT NULL,
    CONSTRAINT pk_game_genres PRIMARY KEY (game_id, genre_id),
    CONSTRAINT fk_gg_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE,
    CONSTRAINT fk_gg_genre FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- Table: orders
-- Description: Records user purchase events.
-- Normal Form: 3NF
-- -----------------------------------------------------
CREATE TABLE orders (
    id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    created_at DATETIME NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT pk_orders PRIMARY KEY (id),
    CONSTRAINT fk_orders_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- Table: activation_keys
-- Description: Inventory stock control for digital product codes.
-- Normal Form: 3NF
-- -----------------------------------------------------
CREATE TABLE activation_keys (
    id CHAR(36) NOT NULL,
    key_value VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    game_id CHAR(36) NOT NULL,
    order_id CHAR(36),
    CONSTRAINT pk_activation_keys PRIMARY KEY (id),
    CONSTRAINT fk_keys_games FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE,
    CONSTRAINT fk_keys_orders FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
