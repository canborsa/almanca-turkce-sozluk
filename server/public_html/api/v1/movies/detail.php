<?php

require_once __DIR__ . '/../../../../includes/Response.php';
require_once __DIR__ . '/../../../../services/MovieService.php';

$id = isset($_GET['id']) ? (int)$_GET['id'] : (isset($_GET['tmdb_id']) ? (int)$_GET['tmdb_id'] : 0);

if ($id <= 0) {
    Response::error('Geçerli bir film ID belirtin.', 400, 'INVALID_ID');
}

$movieService = new MovieService();
$movie = $movieService->getDetail($id);

if (!$movie) {
    Response::error('Film bulunamadı.', 404, 'MOVIE_NOT_FOUND');
}

Response::json($movie, 200);
