-- MySQL Indexes for Optimizing Queries and Filtering Performance

-- Movies Table Indexes
CREATE INDEX `idx_movies_release_year` ON `movies` (`release_year`);
CREATE INDEX `idx_movies_vote_average` ON `movies` (`vote_average`);
CREATE INDEX `idx_movies_vote_count` ON `movies` (`vote_count`);
CREATE INDEX `idx_movies_popularity` ON `movies` (`popularity`);
CREATE INDEX `idx_movies_release_date` ON `movies` (`release_date`);
CREATE INDEX `idx_movies_user_rating` ON `movies` (`weighted_rating`);
CREATE FULLTEXT INDEX `idx_ft_movies_title` ON `movies` (`title`, `original_title`);

-- Composite Indexes for Multi-criteria Advanced Discovery
CREATE INDEX `idx_movies_discover_composite` ON `movies` (`release_year`, `vote_average`, `vote_count`, `runtime`);

-- Ratings Indexes
CREATE INDEX `idx_ratings_user` ON `ratings` (`user_id`);
CREATE INDEX `idx_ratings_movie` ON `ratings` (`movie_id`);

-- Reviews Indexes
CREATE INDEX `idx_reviews_user` ON `reviews` (`user_id`);
CREATE INDEX `idx_reviews_movie` ON `reviews` (`movie_id`);
CREATE INDEX `idx_reviews_created` ON `reviews` (`created_at`);

-- User Interactions
CREATE INDEX `idx_watchlist_user` ON `watchlist` (`user_id`);
CREATE INDEX `idx_favorites_user` ON `favorites` (`user_id`);
CREATE INDEX `idx_watched_user` ON `watched` (`user_id`);
CREATE INDEX `idx_watch_logs_user_date` ON `watch_logs` (`user_id`, `watched_at`);

-- Cast and People
CREATE INDEX `idx_people_name` ON `people` (`name`);
CREATE INDEX `idx_cast_person` ON `movie_cast` (`person_id`);
CREATE INDEX `idx_crew_person` ON `movie_crew` (`person_id`);
