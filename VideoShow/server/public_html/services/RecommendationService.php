<?php
// VideoShow/server/public_html/services/RecommendationService.php

require_once __DIR__ . '/../includes/Database.php';

class RecommendationService {
    private PDO $db;

    public function __construct() {
        $this->db = Database::getInstance();
    }

    public function getPersonalizedRecommendations(int $userId, int $limit = 10): array {
        // Find top rated genres for user
        $stmt = $this->db->prepare("
            SELECT mg.genre_id, COUNT(*) as cnt
            FROM ratings r
            INNER JOIN movie_genres mg ON r.movie_id = mg.movie_id
            WHERE r.user_id = ? AND r.rating >= 3.5
            GROUP BY mg.genre_id
            ORDER BY cnt DESC LIMIT 3
        ");
        $stmt->execute([$userId]);
        $topGenres = $stmt->fetchAll(PDO::FETCH_COLUMN);

        if (empty($topGenres)) {
            // Fallback to top rated movies
            $stmt = $this->db->prepare("SELECT * FROM movies ORDER BY weighted_rating DESC, popularity DESC LIMIT ?");
            $stmt->execute([$limit]);
            return $stmt->fetchAll();
        }

        $placeholders = implode(',', array_fill(0, count($topGenres), '?'));

        // Find movies in these genres that user hasn't watched/rated yet
        $sql = "
            SELECT DISTINCT m.* FROM movies m
            INNER JOIN movie_genres mg ON m.id = mg.movie_id
            WHERE mg.genre_id IN ($placeholders)
            AND m.id NOT IN (SELECT movie_id FROM ratings WHERE user_id = ?)
            AND m.id NOT IN (SELECT movie_id FROM watched WHERE user_id = ?)
            ORDER BY m.vote_average DESC, m.popularity DESC
            LIMIT ?
        ";

        $params = array_merge($topGenres, [$userId, $userId, $limit]);
        $stmt = $this->db->prepare($sql);
        $stmt->execute($params);

        return $stmt->fetchAll();
    }
}
