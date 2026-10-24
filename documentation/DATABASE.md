# Database Documentation

Database Engine: MySQL 8.0 / MariaDB 10.5+ (PDO PHP)
Charset: utf8mb4
Collation: utf8mb4_unicode_ci

## Key Tables Overview

1. `users`: Stores app users and admin roles (`USER`, `MODERATOR`, `ADMIN`).
2. `movies`: Primary movies repository synced from TMDB.
3. `movie_translations`: Multi-language title, overview, and tagline (Turkish support).
4. `genres` & `movie_genres`: Genres list and many-to-many movie relationship.
5. `people`, `movie_cast`, `movie_crew`: Cast and crew database.
6. `ratings`: User movie ratings (0.5 to 5.0 scale).
7. `reviews`: Written reviews with spoiler flag and likes count.
8. `favorites`, `watchlist`, `watched`: User movie interaction lists.
9. `watch_logs`: Detailed film diary tracking dates, rewatches, ratings, and notes.
10. `lists` & `list_movies`: Custom user-curated public or private lists.
11. `reports`: Content moderation reports.
12. `sync_logs`: TMDB background synchronization history and stats.
