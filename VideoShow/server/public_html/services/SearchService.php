<?php
// VideoShow/server/public_html/services/SearchService.php

require_once __DIR__ . '/../includes/Database.php';

class SearchService {
    private PDO $db;

    public function __construct() {
        $this->db = Database::getInstance();
    }

    public function search(string $query, int $limit = 20): array {
        $cleanQuery = trim($query);
        if (empty($cleanQuery)) {
            return ['movies' => [], 'people' => []];
        }

        $likeQuery = '%' . $cleanQuery . '%';

        // Search Movies
        $stmt = $this->db->prepare("
            SELECT * FROM movies
            WHERE title LIKE ? OR original_title LIKE ? OR imdb_id = ? OR tmdb_id = ?
            ORDER BY popularity DESC LIMIT ?
        ");
        $stmt->execute([$likeQuery, $likeQuery, $cleanQuery, (int)$cleanQuery, $limit]);
        $movies = $stmt->fetchAll();

        // Search People
        $stmt = $this->db->prepare("
            SELECT * FROM people
            WHERE name LIKE ? OR tmdb_id = ?
            ORDER BY popularity DESC LIMIT ?
        ");
        $stmt->execute([$likeQuery, (int)$cleanQuery, $limit]);
        $people = $stmt->fetchAll();

        return [
            'movies' => $movies,
            'people' => $people
        ];
    }
}
