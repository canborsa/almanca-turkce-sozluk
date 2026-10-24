<?php
// VideoShow/server/public_html/api/v1/lists/add_movie.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../includes/Validator.php';

$user = Auth::requireAuth();
$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;

$listId = Validator::sanitizeInt($input['list_id'] ?? 0);
$movieId = Validator::sanitizeInt($input['movie_id'] ?? 0);

if (!$listId || !$movieId) Response::error("Liste ID ve Film ID zorunludur.", 400, "VALIDATION_ERROR");

$db = Database::getInstance();
$stmt = $db->prepare("SELECT user_id FROM lists WHERE id = ?");
$stmt->execute([$listId]);
$ownerId = $stmt->fetchColumn();

if ($ownerId != $user['id'] && $user['role'] !== 'ADMIN') {
    Response::error("Bu listeye film ekleme yetkiniz yok.", 403, "FORBIDDEN");
}

$stmt = $db->prepare("INSERT IGNORE INTO list_movies (list_id, movie_id) VALUES (?, ?)");
$stmt->execute([$listId, $movieId]);

Response::json(null, 200, "Film listeye eklendi.");
