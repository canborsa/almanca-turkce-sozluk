<?php
// VideoShow/server/public_html/api/v1/watched/add.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../services/MovieService.php';

$user = Auth::requireAuth();
$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;
$movieId = Validator::sanitizeInt($input['movie_id'] ?? 0);

if (!$movieId) Response::error("Film ID gereklidir.", 400, "MISSING_ID");

$db = Database::getInstance();
$stmt = $db->prepare("INSERT IGNORE INTO watched (user_id, movie_id) VALUES (?, ?)");
$stmt->execute([$user['id'], $movieId]);

// Also record log in watch_logs if provided
$watchedAt = $input['watched_at'] ?? date('Y-m-d');
$rating = isset($input['rating']) ? (float)$input['rating'] : null;
$rewatch = !empty($input['rewatch']) ? 1 : 0;
$notes = Validator::sanitizeString($input['notes'] ?? '');

$logStmt = $db->prepare("
    INSERT INTO watch_logs (user_id, movie_id, watched_at, rating, rewatch, notes)
    VALUES (?, ?, ?, ?, ?, ?)
");
$logStmt->execute([$user['id'], $movieId, $watchedAt, $rating, $rewatch, $notes]);

(new MovieService())->updateMovieStats($movieId);
Response::json(null, 200, "İzlendi olarak işaretlendi ve günlüğe eklendi.");
