<?php
// VideoShow/server/public_html/api/v1/watchlist/add.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../services/MovieService.php';

$user = Auth::requireAuth();
$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;
$movieId = Validator::sanitizeInt($input['movie_id'] ?? 0);

if (!$movieId) Response::error("Film ID gereklidir.", 400, "MISSING_ID");

$db = Database::getInstance();
$stmt = $db->prepare("INSERT IGNORE INTO watchlist (user_id, movie_id) VALUES (?, ?)");
$stmt->execute([$user['id'], $movieId]);

(new MovieService())->updateMovieStats($movieId);
Response::json(null, 200, "İzleme listesine eklendi.");
