-- =====================================================================
-- Movie Platform Database Schema
-- Version: 1.0.0
-- Target MySQL / MariaDB (PHP PDO Compatible)
-- =====================================================================

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

-- 1. USERS TABLE
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) DEFAULT NULL,
    avatar_url VARCHAR(500) DEFAULT NULL,
    bio TEXT DEFAULT NULL,
    role ENUM('USER', 'MODERATOR', 'ADMIN') NOT NULL DEFAULT 'USER',
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at DATETIME DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. MOVIES TABLE
CREATE TABLE movies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tmdb_id INT NOT NULL UNIQUE,
    imdb_id VARCHAR(20) DEFAULT NULL,
    title VARCHAR(255) NOT NULL,
    original_title VARCHAR(255) DEFAULT NULL,
    overview TEXT DEFAULT NULL,
    tagline VARCHAR(500) DEFAULT NULL,
    release_date DATE DEFAULT NULL,
    release_year INT DEFAULT NULL,
    runtime INT DEFAULT NULL,
    poster_path VARCHAR(255) DEFAULT NULL,
    backdrop_path VARCHAR(255) DEFAULT NULL,
    original_language VARCHAR(10) DEFAULT 'en',
    status VARCHAR(50) DEFAULT 'Released',
    adult TINYINT(1) NOT NULL DEFAULT 0,
    vote_average DECIMAL(3, 1) DEFAULT 0.0,
    vote_count INT DEFAULT 0,
    popularity DECIMAL(10, 3) DEFAULT 0.000,
    budget BIGINT DEFAULT 0,
    revenue BIGINT DEFAULT 0,
    homepage VARCHAR(500) DEFAULT NULL,
    tmdb_updated_at DATETIME DEFAULT NULL,
    local_updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    -- Our Platform Stats & Bayesian Rating
    app_vote_average DECIMAL(3, 2) DEFAULT 0.00,
    app_vote_count INT DEFAULT 0,
    weighted_rating DECIMAL(4, 3) DEFAULT 0.000,
    review_count INT DEFAULT 0,
    favorite_count INT DEFAULT 0,
    watchlist_count INT DEFAULT 0,
    watched_count INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. MOVIE TRANSLATIONS TABLE
CREATE TABLE movie_translations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    language_code VARCHAR(10) NOT NULL DEFAULT 'tr',
    title VARCHAR(255) NOT NULL,
    overview TEXT DEFAULT NULL,
    tagline VARCHAR(500) DEFAULT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    UNIQUE KEY uk_movie_lang (movie_id, language_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. GENRES TABLE
CREATE TABLE genres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tmdb_id INT NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. MOVIE_GENRES TABLE
CREATE TABLE movie_genres (
    movie_id INT NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (movie_id, genre_id),
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. PEOPLE TABLE
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
    popularity DECIMAL(10, 3) DEFAULT 0.000
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. MOVIE_CAST TABLE
CREATE TABLE movie_cast (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    person_id INT NOT NULL,
    character_name VARCHAR(255) DEFAULT NULL,
    cast_order INT DEFAULT 0,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (person_id) REFERENCES people(id) ON DELETE CASCADE,
    UNIQUE KEY uk_movie_person_char (movie_id, person_id, cast_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. MOVIE_CREW TABLE
CREATE TABLE movie_crew (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    person_id INT NOT NULL,
    department VARCHAR(100) DEFAULT NULL,
    job VARCHAR(100) DEFAULT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (person_id) REFERENCES people(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. PRODUCTION COMPANIES TABLE
CREATE TABLE production_companies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tmdb_id INT NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    logo_path VARCHAR(255) DEFAULT NULL,
    origin_country VARCHAR(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. MOVIE_PRODUCTION_COMPANIES TABLE
CREATE TABLE movie_production_companies (
    movie_id INT NOT NULL,
    company_id INT NOT NULL,
    PRIMARY KEY (movie_id, company_id),
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES production_companies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. RATINGS TABLE
CREATE TABLE ratings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    rating DECIMAL(2, 1) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_movie_rating (user_id, movie_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. REVIEWS TABLE
CREATE TABLE reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    rating_id INT DEFAULT NULL,
    title VARCHAR(255) DEFAULT NULL,
    body TEXT NOT NULL,
    spoiler TINYINT(1) NOT NULL DEFAULT 0,
    like_count INT DEFAULT 0,
    status ENUM('APPROVED', 'PENDING', 'REJECTED') NOT NULL DEFAULT 'APPROVED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (rating_id) REFERENCES ratings(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. FAVORITES TABLE
CREATE TABLE favorites (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_movie_favorite (user_id, movie_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. WATCHLIST TABLE
CREATE TABLE watchlist (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_movie_watchlist (user_id, movie_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 15. WATCHED TABLE
CREATE TABLE watched (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    watched_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_movie_watched (user_id, movie_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 16. WATCH_LOGS TABLE (Film Günlüğü)
CREATE TABLE watch_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    watched_at DATE NOT NULL,
    rating DECIMAL(2, 1) DEFAULT NULL,
    review_id INT DEFAULT NULL,
    rewatch TINYINT(1) NOT NULL DEFAULT 0,
    notes TEXT DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 17. LISTS TABLE
CREATE TABLE lists (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT DEFAULT NULL,
    is_public TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 18. LIST_MOVIES TABLE
CREATE TABLE list_movies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    list_id INT NOT NULL,
    movie_id INT NOT NULL,
    sort_order INT DEFAULT 0,
    added_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (list_id) REFERENCES lists(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    UNIQUE KEY uk_list_movie (list_id, movie_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 19. REPORTS TABLE
CREATE TABLE reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    review_id INT NOT NULL,
    reason ENUM('Spam', 'Hakaret', 'Spoiler', 'Taciz', 'Uygunsuz içerik', 'Diğer') NOT NULL,
    description TEXT DEFAULT NULL,
    status ENUM('PENDING', 'RESOLVED', 'DISMISSED') NOT NULL DEFAULT 'PENDING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 20. SYNC_LOGS TABLE
CREATE TABLE sync_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sync_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    started_at DATETIME NOT NULL,
    finished_at DATETIME DEFAULT NULL,
    processed INT DEFAULT 0,
    inserted INT DEFAULT 0,
    updated INT DEFAULT 0,
    failed INT DEFAULT 0,
    skipped INT DEFAULT 0,
    api_requests INT DEFAULT 0,
    details TEXT DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
