<?php
// VideoShow/server/public_html/services/ReviewService.php

require_once __DIR__ . '/../includes/Database.php';
require_once __DIR__ . '/MovieService.php';

class ReviewService {
    private PDO $db;
    private MovieService $movieService;

    public function __construct() {
        $this->db = Database::getInstance();
        $this->movieService = new MovieService();
    }

    public function createReview(int $userId, int $movieId, string $body, ?string $title = null, bool $spoiler = false): array {
        if (empty(trim($body))) {
            throw new Exception("Yorum içeriği boş olamaz.");
        }

        // Get user rating if exists
        $stmt = $this->db->prepare("SELECT id FROM ratings WHERE user_id = ? AND movie_id = ?");
        $stmt->execute([$userId, $movieId]);
        $ratingId = $stmt->fetchColumn() ?: null;

        $stmt = $this->db->prepare("
            INSERT INTO reviews (user_id, movie_id, rating_id, title, body, spoiler)
            VALUES (?, ?, ?, ?, ?, ?)
        ");
        $stmt->execute([$userId, $movieId, $ratingId, $title, $body, $spoiler ? 1 : 0]);
        $reviewId = (int)$this->db->lastInsertId();

        $this->movieService->updateMovieStats($movieId);

        return $this->getReviewById($reviewId);
    }

    public function getReviewById(int $reviewId): array {
        $stmt = $this->db->prepare("
            SELECT r.*, u.username, u.display_name, u.avatar_url, rat.rating
            FROM reviews r
            INNER JOIN users u ON r.user_id = u.id
            LEFT JOIN ratings rat ON r.rating_id = rat.id
            WHERE r.id = ?
        ");
        $stmt->execute([$reviewId]);
        $review = $stmt->fetch();
        if (!$review) {
            throw new Exception("Yorum bulunamadı.");
        }
        return $review;
    }

    public function getMovieReviews(int $movieId, int $page = 1, int $perPage = 20, string $sort = 'newest'): array {
        $offset = ($page - 1) * $perPage;

        $orderBy = 'r.created_at DESC';
        if ($sort === 'popular') $orderBy = 'r.like_count DESC';
        if ($sort === 'rating_high') $orderBy = 'rat.rating DESC';
        if ($sort === 'rating_low') $orderBy = 'rat.rating ASC';

        $countStmt = $this->db->prepare("SELECT COUNT(*) FROM reviews WHERE movie_id = ? AND status = 'APPROVED'");
        $countStmt->execute([$movieId]);
        $total = (int)$countStmt->fetchColumn();

        $stmt = $this->db->prepare("
            SELECT r.*, u.username, u.display_name, u.avatar_url, rat.rating
            FROM reviews r
            INNER JOIN users u ON r.user_id = u.id
            LEFT JOIN ratings rat ON r.rating_id = rat.id
            WHERE r.movie_id = ? AND r.status = 'APPROVED'
            ORDER BY $orderBy
            LIMIT $perPage OFFSET $offset
        ");
        $stmt->execute([$movieId]);
        $reviews = $stmt->fetchAll();

        return ['reviews' => $reviews, 'total' => $total];
    }
}
