-- Seed Data for Default Admin and Genres

-- Default Genres (TMDB Genre Mapping)
INSERT INTO `genres` (`id`, `tmdb_id`, `name`) VALUES
(1, 28, 'Aksiyon'),
(2, 12, 'Macera'),
(3, 16, 'Animasyon'),
(4, 35, 'Komedi'),
(5, 80, 'Suç'),
(6, 99, 'Belgesel'),
(7, 18, 'Drama'),
(8, 10751, 'Aile'),
(9, 14, 'Fantastik'),
(10, 36, 'Tarih'),
(11, 27, 'Korku'),
(12, 10402, 'Müzik'),
(13, 9648, 'Gizem'),
(14, 10749, 'Romantik'),
(15, 878, 'Bilim Kurgu'),
(16, 10770, 'TV Filmi'),
(17, 53, 'Gerilim'),
(18, 10752, 'Savaş'),
(19, 37, 'Batı (Western)')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- Default Admin User (Password: Admin123456!)
INSERT INTO `users` (`id`, `username`, `email`, `password_hash`, `display_name`, `role`, `is_active`) VALUES
(1, 'admin', 'admin@example.com', '$2y$10$w3a681d.7VqgLIsN2eWpOuq.3S4l5D6M7N8O9P0Q1R2S3T4U5V6W', 'Platform Administrator', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE `role` = 'ADMIN';

-- Sample Test Movie (Interstellar)
INSERT INTO `movies` (`id`, `tmdb_id`, `imdb_id`, `title`, `original_title`, `overview`, `tagline`, `release_date`, `release_year`, `runtime`, `poster_path`, `backdrop_path`, `original_language`, `status`, `vote_average`, `vote_count`, `popularity`, `user_rating_average`, `user_rating_count`, `weighted_rating`) VALUES
(1, 157336, 'tt0816692', 'Yıldızlararası', 'Interstellar', 'Mankind was born on Earth. It was never meant to die here.', 'Mankind was born on Earth. It was never meant to die here.', '2014-11-05', 2014, 169, '/gEU2QniE6E77NI6lCU6Mxl4vO21.jpg', '/xJHokMbljvjADYdit5fK1R3O23i.jpg', 'en', 'Released', 8.4, 34500, 145.820, 4.75, 120, 4.70)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

INSERT INTO `movie_translations` (`movie_id`, `language_code`, `title`, `overview`, `tagline`) VALUES
(1, 'tr', 'Yıldızlararası', 'İnsanlığın Dünya üzerindeki son günleri yaklaşırken, bir grup kaşif insanlık tarihinin en önemli görevini üstlenir.', 'İnsanlığın doğduğu yer Dünya’dır, ancak burada ölmek zorunda değildir.')
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

INSERT INTO `movie_genres` (`movie_id`, `genre_id`) VALUES
(1, 2), -- Macera
(1, 7), -- Drama
(1, 15) -- Bilim Kurgu
ON DUPLICATE KEY UPDATE `movie_id` = VALUES(`movie_id`);
