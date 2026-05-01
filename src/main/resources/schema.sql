-- ==============================================================================
-- LIMPIEZA PREVIA (Solo para entorno de desarrollo)
-- ==============================================================================
DROP TABLE IF EXISTS favourites;
DROP TABLE IF EXISTS ingredients;
DROP TABLE IF EXISTS elaboration_steps;
DROP TABLE IF EXISTS recipes;
DROP TABLE IF EXISTS users;

-- ==============================================================================
-- ESTRUCTURA DE LA BASE DE DATOS (schema.sql)
-- ==============================================================================

-- 1. Tabla de Usuarios
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname1 VARCHAR(255) NOT NULL,
    surname2 VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    register_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 2. Tabla de Recetas (EN PLURAL Y CON IMAGE Y USER_ID)
CREATE TABLE IF NOT EXISTS recipes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    preparation_time INT NOT NULL,
    category VARCHAR(100),
    image VARCHAR(255),
    user_id BIGINT,
    CONSTRAINT fk_recipe_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
    );

-- 3. Tabla de Ingredientes (Depende de recipes)
CREATE TABLE IF NOT EXISTS ingredients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    quantity VARCHAR(100) NOT NULL,
    recipe_id BIGINT NOT NULL,
    CONSTRAINT fk_ingredients_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
    );

-- 4. Tabla de Pasos de Elaboración (Depende de recipes)
CREATE TABLE IF NOT EXISTS elaboration_steps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    step_number INT NOT NULL,
    instruction TEXT NOT NULL,
    recipe_id BIGINT NOT NULL,
    CONSTRAINT fk_step_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
    );

-- 5. Tabla intermedia para Recetas Favoritas (Many-To-Many)
CREATE TABLE IF NOT EXISTS favourites (
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, recipe_id),
    CONSTRAINT fk_favourites_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_favourites_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
    );