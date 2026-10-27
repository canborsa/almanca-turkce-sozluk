<?php
require_once __DIR__ . '/../includes/Response.php';
require_once __DIR__ . '/../includes/Auth.php';
require_once __DIR__ . '/../includes/Database.php';
require_once __DIR__ . '/../services/TMDBService.php';

$user = Auth::requireAdmin();
$input = json_decode(file_get_contents('php://input'), true);
$tmdbId = (int)($input['tmdb_id'] ?? 0);

if ($tmdbId <= 0) {
    Response::error('Geçersiz TMDB ID');
}

$tmdb = new TMDBService();
$data = $tmdb->getMovieDetails($tmdbId);

if (!$data) {
    Response::error('TMDB Verisi alınamadı.');
}

$db = Database::getInstance();
$stmt = $db->prepare("
    INSERT INTO movies (tmdb_id, imdb_id, title, original_title, overview, tagline, release_date, release_year, runtime, poster_path, backdrop_path, tmdb_vote_average, tmdb_vote_count, popularity)
    VALUES (:tmdb_id, :imdb_id, :title, :original_title, :overview, :tagline, :release_date, :release_year, :runtime, :poster_path, :backdrop_path, :vote_avg, :vote_cnt, :pop)
    ON DUPLICATE KEY UPDATE title = VALUES(title), overview = VALUES(overview), popularity = VALUES(popularity)
");

$stmt->execute([
    ':tmdb_id' => $data['id'],
    ':imdb_id' => $data['imdb_id'] ?? null,
    ':title' => $data['title'],
    ':original_title' => $data['original_title'] ?? $data['title'],
    ':overview' => $data['overview'] ?? '',
    ':tagline' => $data['tagline'] ?? '',
    ':release_date' => !empty($data['release_date']) ? $data['release_date'] : null,
    ':release_year' => !empty($data['release_date']) ? (int)substr($data['release_date'], 0, 4) : null,
    ':runtime' => $data['runtime'] ?? null,
    ':poster_path' => $data['poster_path'] ?? null,
    ':backdrop_path' => $data['backdrop_path'] ?? null,
    ':vote_avg' => $data['vote_average'] ?? 0.0,
    ':vote_cnt' => $data['vote_count'] ?? 0,
    ':pop' => $data['popularity'] ?? 0.0
]);

Response::json(null, true, "Film (#{$tmdbId}) başarıyla aktarıldı.");
