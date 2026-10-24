<?php
// VideoShow/server/public_html/api/v1/movies/similar.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../services/MovieService.php';

$movieId = Validator::sanitizeInt($_GET['id'] ?? 0);
if (!$movieId) {
    Response::error("Film ID gereklidir.", 400, "MISSING_ID");
}

$db = Database::getInstance();
$stmt = $db->prepare("SELECT genre_id FROM movie_genres WHERE movie_id = ?");
$stmt->execute([$movieId]);
$genres = $stmt->fetchAll(PDO::FETCH_COLUMN);

if (empty($genres)) {
    Response::json([], 200);
}

$movieService = new MovieService();
$filters = ['genres' => implode(',', $genres), 'sort_by' => 'popularity'];
$result = $movieService->discoverMovies($filters, 1, 10);

// Exclude current movie
$similar = array_filter($result['movies'], function($m) use ($movieId) {
    return (int)$m['id'] !== $movieId;
});

Response::json(array_values($similar), 200);
