# VideoShow Database Documentation

Engine: InnoDB
Charset: `utf8mb4`
Collation: `utf8mb4_unicode_ci`

## Main Tables & Relationships

1. **`users`**
   - Primary Key: `id` (AUTO_INCREMENT)
   - Unique Keys: `username`, `email`
   - Role Enum: `'USER'`, `'MODERATOR'`, `'ADMIN'`

2. **`movies`**
   - Primary Key: `id` (AUTO_INCREMENT)
   - Unique Key: `tmdb_id`
   - Indexes: `release_year`, `vote_average`, `vote_count`, `popularity`, `weighted_rating`
   - FullText Index: `(title, original_title)`

3. **`ratings`**
   - Unique Composite Key: `(user_id, movie_id)`
   - Foreign Keys: `user_id -> users.id`, `movie_id -> movies.id`

4. **`reviews`**
   - Foreign Keys: `user_id -> users.id`, `movie_id -> movies.id`, `rating_id -> ratings.id`

5. **`watch_logs` (Film Günlüğü)**
   - Stores user watch diary with `watched_at`, `rating`, `rewatch`, `notes`
