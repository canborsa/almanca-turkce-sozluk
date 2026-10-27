<?php
require_once __DIR__ . '/../includes/Database.php';

class MovieService {
    private PDO $db;

    public function __construct() {
        $this->db = Database::getInstance();
    }

    public function getMovies(int $limit = 20, int $offset = 0): array {
        $stmt = $this->db->prepare("SELECT * FROM movies ORDER BY popularity DESC LIMIT :limit OFFSET :offset");
        $stmt->bindValue(':limit', $limit, PDO::PARAM_INT);
        $stmt->bindValue(':offset', $offset, PDO::PARAM_INT);
        $stmt->execute();
        return $stmt->fetchAll();
    }

    public function getMovieDetail(int $id): ?array {
        $stmt = $this->db->prepare("SELECT * FROM movies WHERE id = :id OR tmdb_id = :id LIMIT 1");
        $stmt->execute([':id' => $id]);
        $movie = $stmt->fetch();

        if (!$movie) return null;

        // Fetch genres
        $gStmt = $this->db->prepare("SELECT g.id, g.name FROM genres g JOIN movie_genres mg ON g.id = mg.genre_id WHERE mg.movie_id = :movie_id");
        $gStmt->execute([':movie_id' => $movie['id']]);
        $movie['genres'] = $gStmt->fetchAll();

        // Fetch cast & director
        $cStmt = $this->db->prepare("SELECT p.id, p.name, p.profile_path, mc.character_name FROM people p JOIN movie_cast mc ON p.id = mc.person_id WHERE mc.movie_id = :movie_id ORDER BY mc.cast_order ASC LIMIT 10");
        $cStmt->execute([':movie_id' => $movie['id']]);
        $movie['cast'] = $cStmt->fetchAll();

        $dStmt = $this->db->prepare("SELECT p.id, p.name FROM people p JOIN movie_crew mcr ON p.id = mcr.person_id WHERE mcr.movie_id = :movie_id AND mcr.job = 'Director' LIMIT 1");
        $dStmt->execute([':movie_id' => $movie['id']]);
        $movie['director'] = $dStmt->fetch();

        return $movie;
    }
}
