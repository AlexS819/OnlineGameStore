-- Очистка таблиць перед створенням (для зручності розробки)
DROP TABLE IF EXISTS game_genres;
DROP TABLE IF EXISTS activation_keys;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS games;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS publishers;

-- =====================================================================
-- 1. ТАБЛИЦЯ: publishers (Видавці)
-- Тип: Стрижнева сутність (Strong Entity)
-- Нормальна форма: 3НФ. 
-- Опис 3НФ: Усі поля атомарні (1НФ), залежать від PK id (2НФ), 
-- та не мають транзитивних залежностей (support_email стосується тільки видавця, а не гри).
-- =====================================================================
CREATE TABLE publishers (
    id CHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    website VARCHAR(255),
    support_email VARCHAR(255),
    CONSTRAINT pk_publishers PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- 2. ТАБЛИЦЯ: users (Користувачі)
-- Тип: Стрижнева сутність
-- Нормальна форма: 3НФ.
-- Опис 3НФ: Відповідає 1НФ та 2НФ. Роль винесена у статичний тип/чек, що запобігає транзитивності.
-- =====================================================================
CREATE TABLE users (
    id CHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- Наприклад: 'ADMIN', 'USER'
    CONSTRAINT pk_users PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- 3. ТАБЛИЦЯ: genres (Жанри)
-- Тип: Стрижнева сутність (Довідник)
-- Нормальна форма: 3НФ.
-- Використання: Окрема таблиця замість Enum для реалізації Багато до Багатьох.
-- =====================================================================
CREATE TABLE genres (
    id CHAR(36) NOT NULL,
    name VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT pk_genres PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- 4. ТАБЛИЦЯ: games (Ігри / Цифрові товари)
-- Тип: Характеристика / Залежна сутність
-- Зв'язок: publishers (1) : (Б) games (Один видавець має багато ігор)
-- Нормальна форма: 3НФ.
-- Опис 3НФ: Атрибути ціни та назви залежать суто від id гри. 
-- Не зберігає назву видавця, тільки publisher_id (виключення надлишковості).
-- =====================================================================
CREATE TABLE games (
    id CHAR(36) NOT NULL,
    title VARCHAR(200) NOT NULL,
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    publisher_id CHAR(36) NOT NULL,
    CONSTRAINT pk_games PRIMARY KEY (id),
    CONSTRAINT fk_games_publishers FOREIGN KEY (publisher_id) REFERENCES publishers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- 5. ТАБЛИЦЯ: game_genres (Зв'язок Гра-Жанр)
-- Тип: Асоціативна сутність (Junction Table)
-- Зв'язок: Багато до Багатьох (М:N) між games та genres
-- Нормальна форма: 3НФ.
-- Обґрунтування: Дозволяє одній грі мати декілька жанрів (наприклад RPG + Action).
-- =====================================================================
CREATE TABLE game_genres (
    game_id CHAR(36) NOT NULL,
    genre_id CHAR(36) NOT NULL,
    CONSTRAINT pk_game_genres PRIMARY KEY (game_id, genre_id),
    CONSTRAINT fk_gg_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE,
    CONSTRAINT fk_gg_genre FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- 6. ТАБЛИЦЯ: orders (Замовлення)
-- Тип: Документальна сутність (Подія)
-- Зв'язок: users (1) : (Б) orders (Користувач робить багато замовлень)
-- Нормальна форма: 3НФ.
-- =====================================================================
CREATE TABLE orders (
    id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    created_at DATETIME NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT pk_orders PRIMARY KEY (id),
    CONSTRAINT fk_orders_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- Digital Goods Catalog & Key Accounting Subsystem
-- Database Schema Definition
-- =====================================================================
CREATE TABLE activation_keys (
    id CHAR(36) NOT NULL,
    key_value VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL, -- 'AVAILABLE', 'SOLD', 'REDEEMED'
    game_id CHAR(36) NOT NULL,
    order_id CHAR(36),
    CONSTRAINT pk_activation_keys PRIMARY KEY (id),
    CONSTRAINT fk_keys_games FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE,
    CONSTRAINT fk_keys_orders FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
