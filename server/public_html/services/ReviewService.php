<?php
require_once __DIR__ . '/../includes/Database.php';

class ReviewService {
    private PDO $db;

    public function __construct() {
        $this->db = Database::getInstance();
    }

    public function createReview(int $userId, int $movieId, string $body, ?string $title = null, bool $spoiler = false): bool {
        $stmt = $this->db->prepare("INSERT INTO reviews (user_id, movie_id, title, body, spoiler) VALUES (:user_id, :movie_id, :title, :body, :spoiler)");
        return $stmt->execute([
            ':user_id' => $userId,
            ':movie_id' => $movieId,
            ':title' => $title,
            ':body' => $body,
            ':spoiler' => $spoiler ? 1 : 0
        ]);
    }

    public function getMovieReviews(int $movieId, int $limit = 20, int $offset = 0): array {
        $stmt = $this->db->prepare("SELECT r.*, u.username, u.avatar_url FROM reviews r JOIN users u ON r.user_id = u.id WHERE r.movie_id = :movie_id AND r.status = 'APPROVED' ORDER BY r.created_at DESC LIMIT :limit OFFSET :offset");
        $stmt->bindValue(':movie_id', $movieId, PDO::PARAM_INT);
        $stmt->bindValue(':limit', $limit, PDO::PARAM_INT);
        $stmt->bindValue(':offset', $offset, PDO::PARAM_INT);
        $stmt->execute();
        return $stmt->fetchAll();
    }
}
