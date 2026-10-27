<?php
require_once __DIR__ . '/../includes/Database.php';

class SearchService {
    private PDO $db;

    public function __construct() {
        $this->db = Database::getInstance();
    }

    public function search(string $query, int $limit = 20, int $offset = 0): array {
        $cleanQuery = trim($query);
        if (empty($cleanQuery)) {
            return ['movies' => [], 'people' => []];
        }

        // Fulltext or LIKE fallback for titles
        $movieStmt = $this->db->prepare("
            SELECT id, tmdb_id, title, original_title, release_year, poster_path, app_vote_average, tmdb_vote_average
            FROM movies
            WHERE title LIKE :q OR original_title LIKE :q OR tmdb_id = :exact_id
            ORDER BY popularity DESC
            LIMIT :limit OFFSET :offset
        ");
        $movieStmt->bindValue(':q', '%' . $cleanQuery . '%');
        $movieStmt->bindValue(':exact_id', is_numeric($cleanQuery) ? (int)$cleanQuery : 0, PDO::PARAM_INT);
        $movieStmt->bindValue(':limit', $limit, PDO::PARAM_INT);
        $movieStmt->bindValue(':offset', $offset, PDO::PARAM_INT);
        $movieStmt->execute();
        $movies = $movieStmt->fetchAll();

        // Search People
        $personStmt = $this->db->prepare("
            SELECT id, tmdb_id, name, profile_path, known_for_department
            FROM people
            WHERE name LIKE :q
            ORDER BY popularity DESC
            LIMIT :limit OFFSET :offset
        ");
        $personStmt->bindValue(':q', '%' . $cleanQuery . '%');
        $personStmt->bindValue(':limit', $limit, PDO::PARAM_INT);
        $personStmt->bindValue(':offset', $offset, PDO::PARAM_INT);
        $personStmt->execute();
        $people = $personStmt->fetchAll();

        return [
            'movies' => $movies,
            'people' => $people
        ];
    }
}
