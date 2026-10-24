<?php

require_once __DIR__ . '/../includes/Database.php';
require_once __DIR__ . '/TMDBService.php';

class MovieService {
    private PDO $db;
    private TMDBService $tmdbService;

    public function __construct() {
        $this->db = Database::getInstance();
        $this->tmdbService = new TMDBService();
    }

    public function getList(int $page = 1, int $perPage = 20): array {
        $offset = ($page - 1) * $perPage;

        $countStmt = $this->db->query("SELECT COUNT(*) FROM movies");
        $total = (int)$countStmt->fetchColumn();

        $stmt = $this->db->prepare("
            SELECT m.*, COALESCE(mt.title, m.title) AS title, COALESCE(mt.overview, m.overview) AS overview
            FROM movies m
            LEFT JOIN movie_translations mt ON m.id = mt.movie_id AND mt.language_code = 'tr'
            ORDER BY m.popularity DESC
            LIMIT :limit OFFSET :offset
        ");
        $stmt->bindValue(':limit', $perPage, PDO::PARAM_INT);
        $stmt->bindValue(':offset', $offset, PDO::PARAM_INT);
        $stmt->execute();
        $movies = $stmt->fetchAll();

        return [
            'items' => $movies,
            'total' => $total
        ];
    }

    public function getDetail(int $id): ?array {
        $stmt = $this->db->prepare("
            SELECT m.*, COALESCE(mt.title, m.title) AS title, COALESCE(mt.overview, m.overview) AS overview, COALESCE(mt.tagline, m.tagline) AS tagline
            FROM movies m
            LEFT JOIN movie_translations mt ON m.id = mt.movie_id AND mt.language_code = 'tr'
            WHERE m.id = :id OR m.tmdb_id = :id
            LIMIT 1
        ");
        $stmt->execute(['id' => $id]);
        $movie = $stmt->fetch();

        if (!$movie) return null;

        // Fetch genres
        $genreStmt = $this->db->prepare("
            SELECT g.id, g.tmdb_id, g.name
            FROM genres g
            JOIN movie_genres mg ON g.id = mg.genre_id
            WHERE mg.movie_id = :movie_id
        ");
        $genreStmt->execute(['movie_id' => $movie['id']]);
        $movie['genres'] = $genreStmt->fetchAll();

        // Fetch top cast
        $castStmt = $this->db->prepare("
            SELECT p.id, p.tmdb_id, p.name, p.profile_path, mc.character_name, mc.cast_order
            FROM people p
            JOIN movie_cast mc ON p.id = mc.person_id
            WHERE mc.movie_id = :movie_id
            ORDER BY mc.cast_order ASC
            LIMIT 10
        ");
        $castStmt->execute(['movie_id' => $movie['id']]);
        $movie['cast'] = $castStmt->fetchAll();

        // Fetch directors
        $crewStmt = $this->db->prepare("
            SELECT p.id, p.tmdb_id, p.name, p.profile_path, mc.job
            FROM people p
            JOIN movie_crew mc ON p.id = mc.person_id
            WHERE mc.movie_id = :movie_id AND mc.job = 'Director'
        ");
        $crewStmt->execute(['movie_id' => $movie['id']]);
        $movie['directors'] = $crewStmt->fetchAll();

        return $movie;
    }

    public function discover(array $filters, int $page = 1, int $perPage = 20): array {
        $offset = ($page - 1) * $perPage;
        $where = ["1=1"];
        $params = [];

        if (!empty($filters['genres']) && is_array($filters['genres'])) {
            $genrePlaceholders = implode(',', array_fill(0, count($filters['genres']), '?'));
            $where[] = "m.id IN (SELECT movie_id FROM movie_genres mg JOIN genres g ON mg.genre_id = g.id WHERE g.tmdb_id IN ({$genrePlaceholders}))";
            foreach ($filters['genres'] as $g) {
                $params[] = (int)$g;
            }
        }

        if (!empty($filters['year_start'])) {
            $where[] = "m.release_year >= ?";
            $params[] = (int)$filters['year_start'];
        }

        if (!empty($filters['year_end'])) {
            $where[] = "m.release_year <= ?";
            $params[] = (int)$filters['year_end'];
        }

        if (!empty($filters['min_rating'])) {
            $where[] = "m.vote_average >= ?";
            $params[] = (float)$filters['min_rating'];
        }

        if (!empty($filters['min_votes'])) {
            $where[] = "m.vote_count >= ?";
            $params[] = (int)$filters['min_votes'];
        }

        if (!empty($filters['min_runtime'])) {
            $where[] = "m.runtime >= ?";
            $params[] = (int)$filters['min_runtime'];
        }

        if (!empty($filters['max_runtime'])) {
            $where[] = "m.runtime <= ?";
            $params[] = (int)$filters['max_runtime'];
        }

        $whereClause = implode(" AND ", $where);

        $sort = "m.vote_average DESC, m.vote_count DESC";
        if (isset($filters['sort_by'])) {
            switch ($filters['sort_by']) {
                case 'popularity': $sort = "m.popularity DESC"; break;
                case 'release_date': $sort = "m.release_date DESC"; break;
                case 'vote_count': $sort = "m.vote_count DESC"; break;
                case 'weighted_rating': $sort = "m.weighted_rating DESC"; break;
            }
        }

        // Count query
        $countSql = "SELECT COUNT(DISTINCT m.id) FROM movies m WHERE {$whereClause}";
        $countStmt = $this->db->prepare($countSql);
        $countStmt->execute($params);
        $total = (int)$countStmt->fetchColumn();

        // Data query
        $sql = "
            SELECT m.*, COALESCE(mt.title, m.title) AS title, COALESCE(mt.overview, m.overview) AS overview
            FROM movies m
            LEFT JOIN movie_translations mt ON m.id = mt.movie_id AND mt.language_code = 'tr'
            WHERE {$whereClause}
            ORDER BY {$sort}
            LIMIT {$perPage} OFFSET {$offset}
        ";
        $stmt = $this->db->prepare($sql);
        $stmt->execute($params);
        $movies = $stmt->fetchAll();

        return [
            'items' => $movies,
            'total' => $total
        ];
    }

    public function search(string $query, int $page = 1, int $perPage = 20): array {
        $offset = ($page - 1) * $perPage;
        $searchTerm = '%' . $query . '%';

        $countStmt = $this->db->prepare("
            SELECT COUNT(*) FROM movies m
            LEFT JOIN movie_translations mt ON m.id = mt.movie_id AND mt.language_code = 'tr'
            WHERE m.title LIKE :q1 OR m.original_title LIKE :q2 OR mt.title LIKE :q3 OR m.imdb_id = :exact
        ");
        $countStmt->execute([
            'q1' => $searchTerm,
            'q2' => $searchTerm,
            'q3' => $searchTerm,
            'exact' => $query
        ]);
        $total = (int)$countStmt->fetchColumn();

        $stmt = $this->db->prepare("
            SELECT m.*, COALESCE(mt.title, m.title) AS title, COALESCE(mt.overview, m.overview) AS overview
            FROM movies m
            LEFT JOIN movie_translations mt ON m.id = mt.movie_id AND mt.language_code = 'tr'
            WHERE m.title LIKE :q1 OR m.original_title LIKE :q2 OR mt.title LIKE :q3 OR m.imdb_id = :exact
            ORDER BY m.popularity DESC
            LIMIT :limit OFFSET :offset
        ");
        $stmt->bindValue(':q1', $searchTerm);
        $stmt->bindValue(':q2', $searchTerm);
        $stmt->bindValue(':q3', $searchTerm);
        $stmt->bindValue(':exact', $query);
        $stmt->bindValue(':limit', $perPage, PDO::PARAM_INT);
        $stmt->bindValue(':offset', $offset, PDO::PARAM_INT);
        $stmt->execute();

        return [
            'items' => $stmt->fetchAll(),
            'total' => $total
        ];
    }

    public function importFromTMDB(int $tmdbId): bool {
        $data = $this->tmdbService->fetchMovieDetail($tmdbId, 'tr-TR');
        if (!$data || !isset($data['id'])) return false;

        $this->db->beginTransaction();

        try {
            $year = !empty($data['release_date']) ? (int)substr($data['release_date'], 0, 4) : null;

            $stmt = $this->db->prepare("
                INSERT INTO movies (
                    tmdb_id, imdb_id, title, original_title, overview, tagline,
                    release_date, release_year, runtime, poster_path, backdrop_path,
                    original_language, status, adult, vote_average, vote_count,
                    popularity, budget, revenue, homepage, tmdb_updated_at
                ) VALUES (
                    :tmdb_id, :imdb_id, :title, :original_title, :overview, :tagline,
                    :release_date, :release_year, :runtime, :poster_path, :backdrop_path,
                    :original_language, :status, :adult, :vote_average, :vote_count,
                    :popularity, :budget, :revenue, :homepage, NOW()
                ) ON DUPLICATE KEY UPDATE
                    title = VALUES(title),
                    original_title = VALUES(original_title),
                    overview = VALUES(overview),
                    vote_average = VALUES(vote_average),
                    vote_count = VALUES(vote_count),
                    popularity = VALUES(popularity),
                    tmdb_updated_at = NOW()
            ");

            $stmt->execute([
                'tmdb_id' => $data['id'],
                'imdb_id' => $data['imdb_id'] ?? null,
                'title' => $data['title'] ?? '',
                'original_title' => $data['original_title'] ?? '',
                'overview' => $data['overview'] ?? '',
                'tagline' => $data['tagline'] ?? '',
                'release_date' => !empty($data['release_date']) ? $data['release_date'] : null,
                'release_year' => $year,
                'runtime' => $data['runtime'] ?? null,
                'poster_path' => $data['poster_path'] ?? null,
                'backdrop_path' => $data['backdrop_path'] ?? null,
                'original_language' => $data['original_language'] ?? 'en',
                'status' => $data['status'] ?? 'Released',
                'adult' => !empty($data['adult']) ? 1 : 0,
                'vote_average' => $data['vote_average'] ?? 0.0,
                'vote_count' => $data['vote_count'] ?? 0,
                'popularity' => $data['popularity'] ?? 0.0,
                'budget' => $data['budget'] ?? 0,
                'revenue' => $data['revenue'] ?? 0,
                'homepage' => $data['homepage'] ?? null,
            ]);

            $movieId = $this->db->lastInsertId();
            if (!$movieId) {
                $mStmt = $this->db->prepare("SELECT id FROM movies WHERE tmdb_id = :tmdb_id");
                $mStmt->execute(['tmdb_id' => $data['id']]);
                $movieId = $mStmt->fetchColumn();
            }

            // Translation record
            if ($movieId) {
                $transStmt = $this->db->prepare("
                    INSERT INTO movie_translations (movie_id, language_code, title, overview, tagline)
                    VALUES (:movie_id, 'tr', :title, :overview, :tagline)
                    ON DUPLICATE KEY UPDATE title = VALUES(title), overview = VALUES(overview), tagline = VALUES(tagline)
                ");
                $transStmt->execute([
                    'movie_id' => $movieId,
                    'title' => $data['title'] ?? '',
                    'overview' => $data['overview'] ?? '',
                    'tagline' => $data['tagline'] ?? ''
                ]);
            }

            $this->db->commit();
            return true;
        } catch (Exception $e) {
            $this->db->rollBack();
            return false;
        }
    }
}
