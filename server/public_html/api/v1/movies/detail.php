<?php
require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../services/MovieService.php';

$id = isset($_GET['id']) ? (int)$_GET['id'] : 0;
if ($id <= 0) {
    Response::error('Geçersiz Film ID');
}

$movieService = new MovieService();
$movie = $movieService->getMovieDetail($id);

if (!$movie) {
    Response::error('Film bulunamadı.', 404);
}

Response::json($movie);
