<?php
// VideoShow/server/public_html/services/RatingService.php

require_once __DIR__ . '/../includes/Database.php';
require_once __DIR__ . '/MovieService.php';

class RatingService {
    private PDO $db;
    private MovieService $movieService;

    public function __construct() {
        $this->db = Database::getInstance();
        $this->movieService = new MovieService();
    }

    public function setRating(int $userId, int $movieId, float $rating): array {
        if ($rating < 0.5 || $rating > 5.0) {
            throw new Exception("Puan 0.5 ile 5.0 arasında olmalıdır.");
        }

        $driver = $this->db->getAttribute(PDO::ATTR_DRIVER_NAME);

        if ($driver === 'sqlite') {
            $stmt = $this->db->prepare("
                INSERT INTO ratings (user_id, movie_id, rating)
                VALUES (?, ?, ?)
                ON CONFLICT(user_id, movie_id) DO UPDATE SET rating = excluded.rating, updated_at = CURRENT_TIMESTAMP
            ");
        } else {
            $stmt = $this->db->prepare("
                INSERT INTO ratings (user_id, movie_id, rating)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE rating = VALUES(rating), updated_at = CURRENT_TIMESTAMP
            ");
        }
        $stmt->execute([$userId, $movieId, $rating]);

        // Auto update movie stats
        $this->movieService->updateMovieStats($movieId);

        return ['movie_id' => $movieId, 'rating' => $rating];
    }

    public function deleteRating(int $userId, int $movieId): bool {
        $stmt = $this->db->prepare("DELETE FROM ratings WHERE user_id = ? AND movie_id = ?");
        $stmt->execute([$userId, $movieId]);

        $this->movieService->updateMovieStats($movieId);
        return true;
    }
}
