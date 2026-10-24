-- Database Schema for Movie Platform
-- Engine: InnoDB
-- Charset: utf8mb4

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `sync_logs`;
DROP TABLE IF EXISTS `reports`;
DROP TABLE IF EXISTS `list_movies`;
DROP TABLE IF EXISTS `lists`;
DROP TABLE IF EXISTS `watch_logs`;
DROP TABLE IF EXISTS `watched`;
DROP TABLE IF EXISTS `watchlist`;
DROP TABLE IF EXISTS `favorites`;
DROP TABLE IF EXISTS `reviews`;
DROP TABLE IF EXISTS `ratings`;
DROP TABLE IF EXISTS `movie_production_companies`;
DROP TABLE IF EXISTS `production_companies`;
DROP TABLE IF EXISTS `movie_crew`;
DROP TABLE IF EXISTS `movie_cast`;
DROP TABLE IF EXISTS `people`;
DROP TABLE IF EXISTS `movie_genres`;
DROP TABLE IF EXISTS `genres`;
DROP TABLE IF EXISTS `movie_translations`;
DROP TABLE IF EXISTS `movies`;
DROP TABLE IF EXISTS `users`;

SET FOREIGN_KEY_CHECKS = 1;

-- Users table
CREATE TABLE `users` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `password_hash` VARCHAR(255) NOT NULL,
  `display_name` VARCHAR(100) NOT NULL,
  `avatar_url` VARCHAR(255) DEFAULT NULL,
  `bio` TEXT DEFAULT NULL,
  `role` ENUM('USER', 'MODERATOR', 'ADMIN') NOT NULL DEFAULT 'USER',
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_login_at` DATETIME DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Movies table
CREATE TABLE `movies` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `tmdb_id` INT UNSIGNED NOT NULL UNIQUE,
  `imdb_id` VARCHAR(20) DEFAULT NULL,
  `title` VARCHAR(255) NOT NULL,
  `original_title` VARCHAR(255) NOT NULL,
  `overview` TEXT DEFAULT NULL,
  `tagline` VARCHAR(500) DEFAULT NULL,
  `release_date` DATE DEFAULT NULL,
  `release_year` SMALLINT UNSIGNED DEFAULT NULL,
  `runtime` SMALLINT UNSIGNED DEFAULT NULL,
  `poster_path` VARCHAR(255) DEFAULT NULL,
  `backdrop_path` VARCHAR(255) DEFAULT NULL,
  `original_language` VARCHAR(10) DEFAULT 'en',
  `status` VARCHAR(50) DEFAULT 'Released',
  `adult` TINYINT(1) NOT NULL DEFAULT 0,
  `vote_average` DECIMAL(3, 1) NOT NULL DEFAULT 0.0,
  `vote_count` INT UNSIGNED NOT NULL DEFAULT 0,
  `popularity` DECIMAL(10, 3) NOT NULL DEFAULT 0.000,
  `budget` BIGINT UNSIGNED DEFAULT 0,
  `revenue` BIGINT UNSIGNED DEFAULT 0,
  `homepage` VARCHAR(255) DEFAULT NULL,
  `user_rating_average` DECIMAL(3, 2) NOT NULL DEFAULT 0.00,
  `user_rating_count` INT UNSIGNED NOT NULL DEFAULT 0,
  `weighted_rating` DECIMAL(3, 2) NOT NULL DEFAULT 0.00,
  `tmdb_updated_at` DATETIME DEFAULT NULL,
  `local_updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Movie Translations table (Turkish, etc)
CREATE TABLE `movie_translations` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `movie_id` INT UNSIGNED NOT NULL,
  `language_code` VARCHAR(10) NOT NULL DEFAULT 'tr',
  `title` VARCHAR(255) NOT NULL,
  `overview` TEXT DEFAULT NULL,
  `tagline` VARCHAR(500) DEFAULT NULL,
  UNIQUE KEY `movie_lang_unique` (`movie_id`, `language_code`),
  CONSTRAINT `fk_movie_trans_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Genres table
CREATE TABLE `genres` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `tmdb_id` INT UNSIGNED NOT NULL UNIQUE,
  `name` VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Movie Genres relationship table
CREATE TABLE `movie_genres` (
  `movie_id` INT UNSIGNED NOT NULL,
  `genre_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`movie_id`, `genre_id`),
  CONSTRAINT `fk_mg_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_mg_genre` FOREIGN KEY (`genre_id`) REFERENCES `genres` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- People table (Actors, Directors, Crew)
CREATE TABLE `people` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `tmdb_id` INT UNSIGNED NOT NULL UNIQUE,
  `name` VARCHAR(150) NOT NULL,
  `biography` TEXT DEFAULT NULL,
  `birthday` DATE DEFAULT NULL,
  `deathday` DATE DEFAULT NULL,
  `place_of_birth` VARCHAR(255) DEFAULT NULL,
  `profile_path` VARCHAR(255) DEFAULT NULL,
  `known_for_department` VARCHAR(50) DEFAULT NULL,
  `popularity` DECIMAL(10, 3) NOT NULL DEFAULT 0.000
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Movie Cast table
CREATE TABLE `movie_cast` (
  `movie_id` INT UNSIGNED NOT NULL,
  `person_id` INT UNSIGNED NOT NULL,
  `character_name` VARCHAR(255) DEFAULT NULL,
  `cast_order` INT UNSIGNED DEFAULT 0,
  PRIMARY KEY (`movie_id`, `person_id`, `cast_order`),
  CONSTRAINT `fk_cast_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cast_person` FOREIGN KEY (`person_id`) REFERENCES `people` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Movie Crew table
CREATE TABLE `movie_crew` (
  `movie_id` INT UNSIGNED NOT NULL,
  `person_id` INT UNSIGNED NOT NULL,
  `department` VARCHAR(100) NOT NULL,
  `job` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`movie_id`, `person_id`, `department`, `job`),
  CONSTRAINT `fk_crew_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_crew_person` FOREIGN KEY (`person_id`) REFERENCES `people` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Production Companies table
CREATE TABLE `production_companies` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `tmdb_id` INT UNSIGNED NOT NULL UNIQUE,
  `name` VARCHAR(150) NOT NULL,
  `logo_path` VARCHAR(255) DEFAULT NULL,
  `origin_country` VARCHAR(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Movie Production Companies
CREATE TABLE `movie_production_companies` (
  `movie_id` INT UNSIGNED NOT NULL,
  `company_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`movie_id`, `company_id`),
  CONSTRAINT `fk_mpc_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_mpc_company` FOREIGN KEY (`company_id`) REFERENCES `production_companies` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Ratings table
CREATE TABLE `ratings` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT UNSIGNED NOT NULL,
  `movie_id` INT UNSIGNED NOT NULL,
  `rating` DECIMAL(2, 1) NOT NULL CHECK (`rating` >= 0.5 AND `rating` <= 5.0),
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `user_movie_rating_unique` (`user_id`, `movie_id`),
  CONSTRAINT `fk_ratings_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ratings_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Reviews table
CREATE TABLE `reviews` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT UNSIGNED NOT NULL,
  `movie_id` INT UNSIGNED NOT NULL,
  `rating_id` INT UNSIGNED DEFAULT NULL,
  `title` VARCHAR(255) DEFAULT NULL,
  `body` TEXT NOT NULL,
  `spoiler` TINYINT(1) NOT NULL DEFAULT 0,
  `like_count` INT UNSIGNED NOT NULL DEFAULT 0,
  `status` ENUM('ACTIVE', 'HIDDEN', 'DELETED') NOT NULL DEFAULT 'ACTIVE',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_reviews_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reviews_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reviews_rating` FOREIGN KEY (`rating_id`) REFERENCES `ratings` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Favorites table
CREATE TABLE `favorites` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT UNSIGNED NOT NULL,
  `movie_id` INT UNSIGNED NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `user_movie_fav_unique` (`user_id`, `movie_id`),
  CONSTRAINT `fk_fav_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_fav_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Watchlist table
CREATE TABLE `watchlist` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT UNSIGNED NOT NULL,
  `movie_id` INT UNSIGNED NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `user_movie_wl_unique` (`user_id`, `movie_id`),
  CONSTRAINT `fk_wl_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_wl_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Watched table
CREATE TABLE `watched` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT UNSIGNED NOT NULL,
  `movie_id` INT UNSIGNED NOT NULL,
  `watched_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `user_movie_watched_unique` (`user_id`, `movie_id`),
  CONSTRAINT `fk_watched_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_watched_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Watch Logs (Film Diary) table
CREATE TABLE `watch_logs` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT UNSIGNED NOT NULL,
  `movie_id` INT UNSIGNED NOT NULL,
  `watched_at` DATE NOT NULL,
  `rating` DECIMAL(2, 1) DEFAULT NULL,
  `review_id` INT UNSIGNED DEFAULT NULL,
  `rewatch` TINYINT(1) NOT NULL DEFAULT 0,
  `notes` TEXT DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_wl_logs_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_wl_logs_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_wl_logs_review` FOREIGN KEY (`review_id`) REFERENCES `reviews` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Custom User Lists table
CREATE TABLE `lists` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT UNSIGNED NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT DEFAULT NULL,
  `is_private` TINYINT(1) NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_lists_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Movies inside Custom Lists
CREATE TABLE `list_movies` (
  `list_id` INT UNSIGNED NOT NULL,
  `movie_id` INT UNSIGNED NOT NULL,
  `added_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`list_id`, `movie_id`),
  CONSTRAINT `fk_lm_list` FOREIGN KEY (`list_id`) REFERENCES `lists` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_lm_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Content Moderation Reports
CREATE TABLE `reports` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT UNSIGNED NOT NULL,
  `review_id` INT UNSIGNED NOT NULL,
  `reason` ENUM('Spam', 'Hakaret', 'Spoiler', 'Taciz', 'Uygunsuz icerik', 'Diger') NOT NULL,
  `description` TEXT DEFAULT NULL,
  `status` ENUM('PENDING', 'RESOLVED', 'DISMISSED') NOT NULL DEFAULT 'PENDING',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_reports_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reports_review` FOREIGN KEY (`review_id`) REFERENCES `reviews` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- TMDB & Background Sync Logs
CREATE TABLE `sync_logs` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `task_name` VARCHAR(100) NOT NULL,
  `status` ENUM('RUNNING', 'SUCCESS', 'FAILED') NOT NULL,
  `processed` INT UNSIGNED NOT NULL DEFAULT 0,
  `inserted` INT UNSIGNED NOT NULL DEFAULT 0,
  `updated` INT UNSIGNED NOT NULL DEFAULT 0,
  `failed` INT UNSIGNED NOT NULL DEFAULT 0,
  `skipped` INT UNSIGNED NOT NULL DEFAULT 0,
  `api_requests` INT UNSIGNED NOT NULL DEFAULT 0,
  `error_message` TEXT DEFAULT NULL,
  `started_at` DATETIME NOT NULL,
  `finished_at` DATETIME DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
