<?php
require_once __DIR__ . '/../includes/Database.php';

class RecommendationService {
    private PDO $db;

    public function __construct() {
        $this->db = Database::getInstance();
    }

    public function getPersonalizedRecommendations(int $userId, int $limit = 20): array {
        // Simple content recommendation based on user's highest rated genres
        $stmt = $this->db->prepare("
            SELECT mg.genre_id
            FROM ratings r
            JOIN movie_genres mg ON r.movie_id = mg.movie_id
            WHERE r.user_id = :user_id AND r.rating >= 3.5
            GROUP BY mg.genre_id
            ORDER BY COUNT(*) DESC
            LIMIT 3
        ");
        $stmt->execute([':user_id' => $userId]);
        $favoriteGenres = $stmt->fetchAll(PDO::FETCH_COLUMN);

        if (empty($favoriteGenres)) {
            // Fallback to top rated popular movies
            $fallback = $this->db->prepare("SELECT * FROM movies ORDER BY popularity DESC, app_vote_average DESC LIMIT :limit");
            $fallback->bindValue(':limit', $limit, PDO::PARAM_INT);
            $fallback->execute();
            return $fallback->fetchAll();
        }

        $inClause = implode(',', array_map('intval', $favoriteGenres));
        $recStmt = $this->db->prepare("
            SELECT DISTINCT m.*
            FROM movies m
            JOIN movie_genres mg ON m.id = mg.movie_id
            WHERE mg.genre_id IN ($inClause)
            AND m.id NOT IN (SELECT movie_id FROM watched WHERE user_id = :user_id)
            ORDER BY m.popularity DESC
            LIMIT :limit
        ");
        $recStmt->bindValue(':user_id', $userId, PDO::PARAM_INT);
        $recStmt->bindValue(':limit', $limit, PDO::PARAM_INT);
        $recStmt->execute();

        return $recStmt->fetchAll();
    }
}
