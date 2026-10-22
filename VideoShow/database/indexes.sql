-- =====================================================================
-- Movie Platform Database Indexes
-- Performance optimization for searching, filtering, and joins
-- =====================================================================

-- Movies Indexes
CREATE INDEX idx_movies_tmdb_id ON movies(tmdb_id);
CREATE INDEX idx_movies_imdb_id ON movies(imdb_id);
CREATE INDEX idx_movies_release_year ON movies(release_year);
CREATE INDEX idx_movies_release_date ON movies(release_date);
CREATE INDEX idx_movies_vote_average ON movies(vote_average);
CREATE INDEX idx_movies_vote_count ON movies(vote_count);
CREATE INDEX idx_movies_popularity ON movies(popularity);
CREATE INDEX idx_movies_weighted_rating ON movies(weighted_rating);
CREATE INDEX idx_movies_runtime ON movies(runtime);
CREATE INDEX idx_movies_status ON movies(status);
CREATE FULLTEXT INDEX idx_movies_ft_title ON movies(title, original_title);

-- Movie Translations Indexes
CREATE INDEX idx_movie_trans_lang ON movie_translations(language_code);
CREATE FULLTEXT INDEX idx_movie_trans_ft_title ON movie_translations(title);

-- People Indexes
CREATE INDEX idx_people_tmdb_id ON people(tmdb_id);
CREATE INDEX idx_people_popularity ON people(popularity);
CREATE FULLTEXT INDEX idx_people_ft_name ON people(name);

-- Cast & Crew Indexes
CREATE INDEX idx_cast_movie ON movie_cast(movie_id);
CREATE INDEX idx_cast_person ON movie_cast(person_id);
CREATE INDEX idx_crew_movie ON movie_crew(movie_id);
CREATE INDEX idx_crew_person ON movie_crew(person_id);
CREATE INDEX idx_crew_dept_job ON movie_crew(department, job);

-- User Actions Indexes
CREATE INDEX idx_ratings_user ON ratings(user_id);
CREATE INDEX idx_ratings_movie ON ratings(movie_id);
CREATE INDEX idx_reviews_user ON reviews(user_id);
CREATE INDEX idx_reviews_movie ON reviews(movie_id);
CREATE INDEX idx_reviews_status ON reviews(status);
CREATE INDEX idx_favorites_user ON favorites(user_id);
CREATE INDEX idx_watchlist_user ON watchlist(user_id);
CREATE INDEX idx_watched_user ON watched(user_id);
CREATE INDEX idx_watch_logs_user ON watch_logs(user_id);
CREATE INDEX idx_watch_logs_movie ON watch_logs(movie_id);
CREATE INDEX idx_lists_user ON lists(user_id);
CREATE INDEX idx_list_movies_list ON list_movies(list_id);
CREATE INDEX idx_reports_status ON reports(status);
