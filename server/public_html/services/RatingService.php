<?php
require_once __DIR__ . '/../includes/Database.php';

class RatingService {
    private PDO $db;

    public function __construct() {
        $this->db = Database::getInstance();
    }

    public function rateMovie(int $userId, int $movieId, float $rating): bool {
        $rating = max(0.5, min(5.0, $rating));
        $stmt = $this->db->prepare("INSERT INTO ratings (user_id, movie_id, rating) VALUES (:user_id, :movie_id, :rating) ON DUPLICATE KEY UPDATE rating = :rating, updated_at = CURRENT_TIMESTAMP");
        $success = $stmt->execute([
            ':user_id' => $userId,
            ':movie_id' => $movieId,
            ':rating' => $rating
        ]);

        if ($success) {
            $this->recalculateMovieRating($movieId);
        }

        return $success;
    }

    private function recalculateMovieRating(int $movieId): void {
        $stmt = $this->db->prepare("SELECT AVG(rating) as avg_rating, COUNT(*) as cnt FROM ratings WHERE movie_id = :movie_id");
        $stmt->execute([':movie_id' => $movieId]);
        $stats = $stmt->fetch();

        $update = $this->db->prepare("UPDATE movies SET app_vote_average = :avg, app_vote_count = :cnt WHERE id = :movie_id");
        $update->execute([
            ':avg' => round($stats['avg_rating'] ?? 0.0, 2),
            ':cnt' => $stats['cnt'] ?? 0,
            ':movie_id' => $movieId
        ]);
    }
}
