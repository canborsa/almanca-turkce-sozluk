-- Movie Platform Database Schema

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS sync_logs;
DROP TABLE IF EXISTS reports;
DROP TABLE IF EXISTS list_movies;
DROP TABLE IF EXISTS lists;
DROP TABLE IF EXISTS watch_logs;
DROP TABLE IF EXISTS watched;
DROP TABLE IF EXISTS watchlist;
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS ratings;
DROP TABLE IF EXISTS movie_production_companies;
DROP TABLE IF EXISTS production_companies;
DROP TABLE IF EXISTS movie_crew;
DROP TABLE IF EXISTS movie_cast;
DROP TABLE IF EXISTS people;
DROP TABLE IF EXISTS movie_genres;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS movie_translations;
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) DEFAULT NULL,
    avatar_url VARCHAR(255) DEFAULT NULL,
    bio TEXT DEFAULT NULL,
    role ENUM('USER', 'MODERATOR', 'ADMIN') NOT NULL DEFAULT 'USER',
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE movies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tmdb_id INT NOT NULL UNIQUE,
    imdb_id VARCHAR(20) DEFAULT NULL,
    title VARCHAR(255) NOT NULL,
    original_title VARCHAR(255) DEFAULT NULL,
    overview TEXT DEFAULT NULL,
    tagline VARCHAR(255) DEFAULT NULL,
    release_date DATE DEFAULT NULL,
    release_year SMALLINT DEFAULT NULL,
    runtime INT DEFAULT NULL,
    poster_path VARCHAR(255) DEFAULT NULL,
    backdrop_path VARCHAR(255) DEFAULT NULL,
    original_language VARCHAR(10) DEFAULT 'en',
    status VARCHAR(50) DEFAULT 'Released',
    adult TINYINT(1) DEFAULT 0,
    tmdb_vote_average DECIMAL(3,1) DEFAULT 0.0,
    tmdb_vote_count INT DEFAULT 0,
    app_vote_average DECIMAL(3,2) DEFAULT 0.00,
    app_vote_count INT DEFAULT 0,
    weighted_rating DECIMAL(4,3) DEFAULT 0.000,
    popularity DECIMAL(10,3) DEFAULT 0.000,
    budget BIGINT DEFAULT 0,
    revenue BIGINT DEFAULT 0,
    homepage VARCHAR(255) DEFAULT NULL,
    tmdb_updated_at TIMESTAMP NULL DEFAULT NULL,
    local_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE movie_translations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    language_code VARCHAR(10) NOT NULL,
    title VARCHAR(255) NOT NULL,
    overview TEXT DEFAULT NULL,
    tagline VARCHAR(255) DEFAULT NULL,
    UNIQUE KEY uq_movie_lang (movie_id, language_code),
    CONSTRAINT fk_trans_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE genres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tmdb_id INT NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE movie_genres (
    movie_id INT NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (movie_id, genre_id),
    CONSTRAINT fk_mg_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    CONSTRAINT fk_mg_genre FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE people (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tmdb_id INT NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    biography TEXT DEFAULT NULL,
    birthday DATE DEFAULT NULL,
    deathday DATE DEFAULT NULL,
    place_of_birth VARCHAR(255) DEFAULT NULL,
    profile_path VARCHAR(255) DEFAULT NULL,
    known_for_department VARCHAR(100) DEFAULT NULL,
    popularity DECIMAL(10,3) DEFAULT 0.000
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE movie_cast (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    person_id INT NOT NULL,
    character_name VARCHAR(255) DEFAULT NULL,
    cast_order INT DEFAULT 0,
    CONSTRAINT fk_mc_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    CONSTRAINT fk_mc_person FOREIGN KEY (person_id) REFERENCES people(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE movie_crew (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    person_id INT NOT NULL,
    department VARCHAR(100) NOT NULL,
    job VARCHAR(100) NOT NULL,
    CONSTRAINT fk_mcr_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    CONSTRAINT fk_mcr_person FOREIGN KEY (person_id) REFERENCES people(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE production_companies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tmdb_id INT NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    logo_path VARCHAR(255) DEFAULT NULL,
    origin_country VARCHAR(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE movie_production_companies (
    movie_id INT NOT NULL,
    company_id INT NOT NULL,
    PRIMARY KEY (movie_id, company_id),
    CONSTRAINT fk_mpc_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    CONSTRAINT fk_mpc_company FOREIGN KEY (company_id) REFERENCES production_companies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ratings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    rating DECIMAL(2,1) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_movie_rating (user_id, movie_id),
    CONSTRAINT fk_rating_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_rating_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    rating_id INT DEFAULT NULL,
    title VARCHAR(255) DEFAULT NULL,
    body TEXT NOT NULL,
    spoiler TINYINT(1) DEFAULT 0,
    like_count INT DEFAULT 0,
    status ENUM('APPROVED', 'PENDING', 'HIDDEN') DEFAULT 'APPROVED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_rating FOREIGN KEY (rating_id) REFERENCES ratings(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE favorites (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_movie_fav (user_id, movie_id),
    CONSTRAINT fk_fav_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_fav_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE watchlist (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_movie_wl (user_id, movie_id),
    CONSTRAINT fk_wl_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_wl_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE watched (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_movie_watched (user_id, movie_id),
    CONSTRAINT fk_watched_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_watched_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE watch_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    watched_at DATE NOT NULL,
    rating DECIMAL(2,1) DEFAULT NULL,
    review_id INT DEFAULT NULL,
    rewatch TINYINT(1) DEFAULT 0,
    notes TEXT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_log_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    CONSTRAINT fk_log_review FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE lists (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT DEFAULT NULL,
    is_public TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_list_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE list_movies (
    list_id INT NOT NULL,
    movie_id INT NOT NULL,
    item_order INT DEFAULT 0,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (list_id, movie_id),
    CONSTRAINT fk_lm_list FOREIGN KEY (list_id) REFERENCES lists(id) ON DELETE CASCADE,
    CONSTRAINT fk_lm_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    review_id INT NOT NULL,
    reason ENUM('Spam', 'Hakaret', 'Spoiler', 'Taciz', 'Uygunsuz içerik', 'Diğer') NOT NULL,
    description TEXT DEFAULT NULL,
    status ENUM('PENDING', 'RESOLVED', 'DISMISSED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rep_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_rep_review FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE sync_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sync_type VARCHAR(50) NOT NULL,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP NULL DEFAULT NULL,
    status ENUM('RUNNING', 'SUCCESS', 'FAILED') DEFAULT 'RUNNING',
    processed INT DEFAULT 0,
    inserted INT DEFAULT 0,
    updated INT DEFAULT 0,
    failed INT DEFAULT 0,
    skipped INT DEFAULT 0,
    api_requests INT DEFAULT 0,
    message TEXT DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
