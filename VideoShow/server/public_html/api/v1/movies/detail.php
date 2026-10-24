<?php
// VideoShow/server/public_html/api/v1/movies/detail.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../services/MovieService.php';

$id = Validator::sanitizeInt($_GET['id'] ?? 0);
if (!$id) {
    Response::error("Film ID'si gereklidir.", 400, "MISSING_ID");
}

$movieService = new MovieService();
$movie = $movieService->getMovieDetail($id);

if (!$movie) {
    Response::error("Film bulunamadı.", 404, "MOVIE_NOT_FOUND");
}

// Optional auth context for user actions
$userToken = Auth::getBearerToken();
if ($userToken && ($user = Auth::validateToken($userToken))) {
    $db = Database::getInstance();
    $userId = $user['id'];

    $stmt = $db->prepare("SELECT rating FROM ratings WHERE user_id = ? AND movie_id = ?");
    $stmt->execute([$userId, $id]);
    $movie['user_rating'] = $stmt->fetchColumn() ?: null;

    $stmt = $db->prepare("SELECT 1 FROM favorites WHERE user_id = ? AND movie_id = ?");
    $stmt->execute([$userId, $id]);
    $movie['is_favorite'] = (bool)$stmt->fetchColumn();

    $stmt = $db->prepare("SELECT 1 FROM watchlist WHERE user_id = ? AND movie_id = ?");
    $stmt->execute([$userId, $id]);
    $movie['in_watchlist'] = (bool)$stmt->fetchColumn();

    $stmt = $db->prepare("SELECT 1 FROM watched WHERE user_id = ? AND movie_id = ?");
    $stmt->execute([$userId, $id]);
    $movie['is_watched'] = (bool)$stmt->fetchColumn();
}

Response::json($movie, 200);
