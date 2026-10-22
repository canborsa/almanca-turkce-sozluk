<?php
// VideoShow/server/public_html/api/v1/watched/remove.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../services/MovieService.php';

$user = Auth::requireAuth();
$input = json_decode(file_get_contents('php://input'), true) ?? $_GET;
$movieId = Validator::sanitizeInt($input['movie_id'] ?? 0);

if (!$movieId) Response::error("Film ID gereklidir.", 400, "MISSING_ID");

$db = Database::getInstance();
$stmt = $db->prepare("DELETE FROM watched WHERE user_id = ? AND movie_id = ?");
$stmt->execute([$user['id'], $movieId]);

(new MovieService())->updateMovieStats($movieId);
Response::json(null, 200, "İzlenenlerden çıkarıldı.");
