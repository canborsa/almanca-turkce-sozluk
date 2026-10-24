<?php

require_once __DIR__ . '/../../includes/Response.php';
require_once __DIR__ . '/../../includes/Auth.php';
require_once __DIR__ . '/../../includes/Validator.php';
require_once __DIR__ . '/../../services/MovieService.php';

Auth::requireAdmin();

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    Response::error('Yalnızca POST istekleri desteklenmektedir.', 405, 'METHOD_NOT_ALLOWED');
}

$input = Validator::getJsonInput();
$tmdbId = isset($input['tmdb_id']) ? (int)$input['tmdb_id'] : 0;

if ($tmdbId <= 0) {
    Response::error('Geçerli bir TMDB ID girin.', 400, 'INVALID_TMDB_ID');
}

$movieService = new MovieService();
$success = $movieService->importFromTMDB($tmdbId);

if (!$success) {
    Response::error('Film TMDB\'den aktarılamadı.', 500, 'IMPORT_FAILED');
}

Response::json(['tmdb_id' => $tmdbId], 200, 'Film başarıyla veritabanına aktarıldı.');
