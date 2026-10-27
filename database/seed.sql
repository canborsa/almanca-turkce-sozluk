-- Movie Platform Initial Seed Data

-- Admin and Test Users
INSERT INTO users (id, username, email, password_hash, display_name, role, is_active) VALUES
(1, 'admin', 'admin@movieplatform.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Sistem Yöneticisi', 'ADMIN', 1),
(2, 'testuser', 'user@movieplatform.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Test Kullanıcı', 'USER', 1);

-- Default TMDB Movie Genres
INSERT INTO genres (id, tmdb_id, name) VALUES
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
(19, 37, 'Vahşi Batı');

-- Seed Movie Sample: Interstellar
INSERT INTO movies (id, tmdb_id, imdb_id, title, original_title, overview, tagline, release_date, release_year, runtime, poster_path, backdrop_path, original_language, status, adult, tmdb_vote_average, tmdb_vote_count, app_vote_average, app_vote_count, weighted_rating, popularity, budget, revenue) VALUES
(1, 157336, 'tt0816692', 'Yıldızlararası', 'Interstellar', 'İnsanlığın son günlerinde bir grup kaşif, insan soyunun devamını sağlamak adına galaksinin ötesine seyahat eder.', 'İnsanlığın doğduğu yer sonu olmayacak.', '2014-11-05', 2014, 169, '/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg', '/xJHokMbljvjADYdit5fK1R3O3pA.jpg', 'en', 'Released', 0, 8.4, 34000, 4.50, 10, 4.500, 150.000, 165000000, 677471339);

INSERT INTO movie_genres (movie_id, genre_id) VALUES (1, 7), (1, 15);

INSERT INTO people (id, tmdb_id, name, biography, birthday, place_of_birth, profile_path, known_for_department, popularity) VALUES
(1, 525, 'Christopher Nolan', 'İngiliz ve Amerikalı film yönetmeni, senarist ve yapımcı.', '1970-07-30', 'London, England, UK', '/xuAIuYSmsUzKlRBFm2P3P32S2dE.jpg', 'Directing', 45.000),
(2, 10297, 'Matthew McConaughey', 'Amerikalı oyuncu ve yapımcı.', '1969-11-04', 'Uvalde, Texas, USA', '/sY243A3A9R32ipO22OiL14.jpg', 'Acting', 38.000);

INSERT INTO movie_crew (movie_id, person_id, department, job) VALUES (1, 1, 'Directing', 'Director');
INSERT INTO movie_cast (movie_id, person_id, character_name, cast_order) VALUES (1, 2, 'Joseph Cooper', 0);
