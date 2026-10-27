# Database Documentation

## Tables Overview

### `users`
Stores user profile and credentials.
- **Primary Key:** `id`
- **Columns:** `username`, `email`, `password_hash`, `display_name`, `avatar_url`, `bio`, `role` ('USER', 'MODERATOR', 'ADMIN'), `is_active`, `created_at`, `updated_at`, `last_login_at`.
- **Unique Indexes:** `username`, `email`.

### `movies`
Stores cached movie records imported from TMDB and customized locally.
- **Primary Key:** `id`
- **Columns:** `tmdb_id` (Unique), `imdb_id`, `title`, `original_title`, `overview`, `tagline`, `release_date`, `release_year`, `runtime`, `poster_path`, `backdrop_path`, `original_language`, `status`, `adult`, `tmdb_vote_average`, `tmdb_vote_count`, `app_vote_average`, `app_vote_count`, `weighted_rating`, `popularity`, `budget`, `revenue`, `homepage`, `tmdb_updated_at`, `local_updated_at`, `created_at`.
- **Indexes:** `release_year`, `app_vote_average`, `app_vote_count`, `popularity`, `release_date`, Fulltext on (`title`, `original_title`).

### `movie_translations`
Stores Turkish/multi-language titles and overviews.
- **Foreign Key:** `movie_id` -> `movies(id)` ON DELETE CASCADE.
- **Unique Constraint:** (`movie_id`, `language_code`).

### `genres` & `movie_genres`
Movie genres lookup and many-to-many junction table.
- **Composite Key:** (`movie_id`, `genre_id`).

### `people`, `movie_cast`, `movie_crew`
Actors and Directors/Crew members data.
- **Indexes:** Fulltext on `people.name`.

### `ratings`
User movie rating system (0.5 to 5.0).
- **Unique Constraint:** (`user_id`, `movie_id`).
- **Foreign Keys:** `user_id` -> `users(id)`, `movie_id` -> `movies(id)`.

### `reviews`
User written reviews with spoiler flag and report status.
- **Foreign Keys:** `user_id`, `movie_id`, `rating_id`.

### `favorites`, `watchlist`, `watched`, `watch_logs`
User interaction tables, history, and Letterboxd-style watch logging.

### `lists` & `list_movies`
Custom public/private user created movie lists.

### `reports` & `sync_logs`
Review moderation reports and automated background cron import logs.
