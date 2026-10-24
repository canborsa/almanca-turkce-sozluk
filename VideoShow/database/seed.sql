-- Seed default admin and sample genres
INSERT INTO users (username, email, password_hash, display_name, role, is_active)
VALUES ('admin', 'admin@videoshow.com', '$2y$10$e0MYzXyjpJS7Pd0RVvHwHe11x3F72Fp9N468.o4fG0v58E0r5a.2C', 'Admin User', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE id=id;

-- Seed TMDB Genres
INSERT INTO genres (tmdb_id, name) VALUES
(28, 'Aksiyon'),
(12, 'Macera'),
(16, 'Animasyon'),
(35, 'Komedi'),
(80, 'Suç'),
(99, 'Belgesel'),
(18, 'Drama'),
(10751, 'Aile'),
(14, 'Fantastik'),
(36, 'Tarih'),
(27, 'Korku'),
(10402, 'Müzik'),
(9648, 'Gizem'),
(10749, 'Romantik'),
(878, 'Bilim Kurgu'),
(10770, 'TV Filmi'),
(53, 'Gerilim'),
(10752, 'Savaş'),
(37, 'Batı (Western)')
ON DUPLICATE KEY UPDATE name=VALUES(name);
