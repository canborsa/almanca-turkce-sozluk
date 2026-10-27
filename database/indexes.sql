-- Movie Platform Database Indexes

ALTER TABLE movies ADD INDEX idx_tmdb_id (tmdb_id);
ALTER TABLE movies ADD INDEX idx_release_year (release_year);
ALTER TABLE movies ADD INDEX idx_app_vote_avg (app_vote_average);
ALTER TABLE movies ADD INDEX idx_app_vote_cnt (app_vote_count);
ALTER TABLE movies ADD INDEX idx_tmdb_vote_avg (tmdb_vote_average);
ALTER TABLE movies ADD INDEX idx_popularity (popularity);
ALTER TABLE movies ADD INDEX idx_release_date (release_date);
ALTER TABLE movies ADD FULLTEXT INDEX ft_movies_title (title, original_title);

ALTER TABLE people ADD INDEX idx_people_tmdb_id (tmdb_id);
ALTER TABLE people ADD FULLTEXT INDEX ft_people_name (name);

ALTER TABLE ratings ADD INDEX idx_ratings_user (user_id);
ALTER TABLE ratings ADD INDEX idx_ratings_movie (movie_id);

ALTER TABLE reviews ADD INDEX idx_reviews_user (user_id);
ALTER TABLE reviews ADD INDEX idx_reviews_movie (movie_id);
ALTER TABLE reviews ADD INDEX idx_reviews_status (status);

ALTER TABLE watchlist ADD INDEX idx_wl_user (user_id);
ALTER TABLE watchlist ADD INDEX idx_wl_movie (movie_id);

ALTER TABLE favorites ADD INDEX idx_fav_user (user_id);
ALTER TABLE favorites ADD INDEX idx_fav_movie (movie_id);

ALTER TABLE watched ADD INDEX idx_watched_user (user_id);
ALTER TABLE watched ADD INDEX idx_watched_movie (movie_id);

ALTER TABLE watch_logs ADD INDEX idx_wlog_user (user_id);
ALTER TABLE watch_logs ADD INDEX idx_wlog_movie (movie_id);
ALTER TABLE watch_logs ADD INDEX idx_wlog_date (watched_at);
