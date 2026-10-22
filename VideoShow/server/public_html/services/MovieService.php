<?php
// VideoShow/server/public_html/services/MovieService.php

require_once __DIR__ . '/../includes/Database.php';

class MovieService {
    private PDO $db;

    public function __construct() {
        $this->db = Database::getInstance();
    }

    public function getMovieDetail(int $id): ?array {
        $stmt = $this->db->prepare("SELECT * FROM movies WHERE id = ?");
        $stmt->execute([$id]);
        $movie = $stmt->fetch();

        if (!$movie) return null;

        // Fetch genres
        $stmt = $this->db->prepare("
            SELECT g.* FROM genres g
            INNER JOIN movie_genres mg ON g.id = mg.genre_id
            WHERE mg.movie_id = ?
        ");
        $stmt->execute([$id]);
        $movie['genres'] = $stmt->fetchAll();

        // Fetch cast
        $stmt = $this->db->prepare("
            SELECT p.id, p.tmdb_id, p.name, p.profile_path, mc.character_name, mc.cast_order
            FROM people p
            INNER JOIN movie_cast mc ON p.id = mc.person_id
            WHERE mc.movie_id = ?
            ORDER BY mc.cast_order ASC LIMIT 15
        ");
        $stmt->execute([$id]);
        $movie['cast'] = $stmt->fetchAll();

        // Fetch crew (directors, etc.)
        $stmt = $this->db->prepare("
            SELECT p.id, p.tmdb_id, p.name, p.profile_path, mc.department, mc.job
            FROM people p
            INNER JOIN movie_crew mc ON p.id = mc.person_id
            WHERE mc.movie_id = ? AND mc.job = 'Director'
        ");
        $stmt->execute([$id]);
        $movie['directors'] = $stmt->fetchAll();

        // Distribution of rating
        $stmt = $this->db->prepare("
            SELECT FLOOR(rating) as star, COUNT(*) as count
            FROM ratings
            WHERE movie_id = ?
            GROUP BY FLOOR(rating)
        ");
        $stmt->execute([$id]);
        $distribution = [1=>0, 2=>0, 3=>0, 4=>0, 5=>0];
        foreach ($stmt->fetchAll() as $row) {
            $star = (int)$row['star'];
            if ($star >= 1 && $star <= 5) {
                $distribution[$star] = (int)$row['count'];
            }
        }
        $movie['rating_distribution'] = $distribution;

        return $movie;
    }

    public function updateMovieStats(int $movieId): void {
        // Calculate app_vote_average, app_vote_count
        $stmt = $this->db->prepare("SELECT AVG(rating) as avg_rating, COUNT(*) as total_count FROM ratings WHERE movie_id = ?");
        $stmt->execute([$movieId]);
        $stat = $stmt->fetch();

        $appAvg = round($stat['avg_rating'] ?: 0.0, 2);
        $appCount = (int)($stat['total_count'] ?: 0);

        // Bayesian Weighted Rating calculation
        // W = (v / (v + m)) * R + (m / (v + m)) * C
        // v = number of votes for movie, m = minimum votes required (e.g., 5), R = average rating of movie, C = mean vote across all movies (e.g., 3.5)
        $m = 5;
        $c = 3.5;
        $weightedRating = ($appCount > 0) ? (($appCount / ($appCount + $m)) * $appAvg + ($m / ($appCount + $m)) * $c) : 0.0;

        // Counts for favorites, watchlist, watched, reviews
        $favCount = $this->getCount("SELECT COUNT(*) FROM favorites WHERE movie_id = ?", $movieId);
        $watchlistCount = $this->getCount("SELECT COUNT(*) FROM watchlist WHERE movie_id = ?", $movieId);
        $watchedCount = $this->getCount("SELECT COUNT(*) FROM watched WHERE movie_id = ?", $movieId);
        $reviewCount = $this->getCount("SELECT COUNT(*) FROM reviews WHERE movie_id = ? AND status = 'APPROVED'", $movieId);

        $updateStmt = $this->db->prepare("
            UPDATE movies SET
                app_vote_average = ?,
                app_vote_count = ?,
                weighted_rating = ?,
                favorite_count = ?,
                watchlist_count = ?,
                watched_count = ?,
                review_count = ?
            WHERE id = ?
        ");
        $updateStmt->execute([$appAvg, $appCount, $weightedRating, $favCount, $watchlistCount, $watchedCount, $reviewCount, $movieId]);
    }

    private function getCount(string $sql, int $movieId): int {
        $stmt = $this->db->prepare($sql);
        $stmt->execute([$movieId]);
        return (int)$stmt->fetchColumn();
    }

    public function discoverMovies(array $filters, int $page = 1, int $perPage = 20): array {
        $where = ["1=1"];
        $params = [];

        // Genre Filter
        if (!empty($filters['genres'])) {
            $genreIds = array_map('intval', explode(',', $filters['genres']));
            $placeholders = implode(',', array_fill(0, count($genreIds), '?'));
            $where[] = "m.id IN (SELECT movie_id FROM movie_genres WHERE genre_id IN ($placeholders))";
            $params = array_merge($params, $genreIds);
        }

        // Year Range Filter
        if (!empty($filters['year_min'])) {
            $where[] = "m.release_year >= ?";
            $params[] = (int)$filters['year_min'];
        }
        if (!empty($filters['year_max'])) {
            $where[] = "m.release_year <= ?";
            $params[] = (int)$filters['year_max'];
        }

        // Rating Filter
        if (!empty($filters['min_rating'])) {
            $where[] = "m.vote_average >= ?";
            $params[] = (float)$filters['min_rating'];
        }

        // Vote Count Filter
        if (!empty($filters['min_votes'])) {
            $where[] = "m.vote_count >= ?";
            $params[] = (int)$filters['min_votes'];
        }

        // Runtime Filter
        if (!empty($filters['runtime_min'])) {
            $where[] = "m.runtime >= ?";
            $params[] = (int)$filters['runtime_min'];
        }
        if (!empty($filters['runtime_max'])) {
            $where[] = "m.runtime <= ?";
            $params[] = (int)$filters['runtime_max'];
        }

        // Language Filter
        if (!empty($filters['language'])) {
            $where[] = "m.original_language = ?";
            $params[] = $filters['language'];
        }

        // Director Filter
        if (!empty($filters['director_id'])) {
            $where[] = "m.id IN (SELECT movie_id FROM movie_crew WHERE person_id = ? AND job = 'Director')";
            $params[] = (int)$filters['director_id'];
        }

        // Actor Filter
        if (!empty($filters['actor_id'])) {
            $where[] = "m.id IN (SELECT movie_id FROM movie_cast WHERE person_id = ?)";
            $params[] = (int)$filters['actor_id'];
        }

        $whereClause = implode(' AND ', $where);

        // Sort
        $sortColumn = 'm.popularity';
        $sortOrder = 'DESC';

        if (!empty($filters['sort_by'])) {
            switch ($filters['sort_by']) {
                case 'vote_average': $sortColumn = 'm.vote_average'; break;
                case 'weighted_rating': $sortColumn = 'm.weighted_rating'; break;
                case 'release_date': $sortColumn = 'm.release_date'; break;
                case 'vote_count': $sortColumn = 'm.vote_count'; break;
                case 'title': $sortColumn = 'm.title'; $sortOrder = 'ASC'; break;
            }
        }

        // Count total
        $countSql = "SELECT COUNT(*) FROM movies m WHERE $whereClause";
        $countStmt = $this->db->prepare($countSql);
        $countStmt->execute($params);
        $total = (int)$countStmt->fetchColumn();

        // Data query
        $offset = ($page - 1) * $perPage;
        $sql = "SELECT m.* FROM movies m WHERE $whereClause ORDER BY $sortColumn $sortOrder LIMIT $perPage OFFSET $offset";
        $stmt = $this->db->prepare($sql);
        $stmt->execute($params);
        $movies = $stmt->fetchAll();

        return [
            'movies' => $movies,
            'total'  => $total
        ];
    }
}
