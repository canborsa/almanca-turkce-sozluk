<?php
// VideoShow/server/public_html/api/v1/reviews/delete.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../includes/Database.php';

$user = Auth::requireAuth();
$input = json_decode(file_get_contents('php://input'), true) ?? $_GET;

$reviewId = Validator::sanitizeInt($input['id'] ?? 0);
if (!$reviewId) {
    Response::error("Yorum ID gereklidir.", 400, "MISSING_ID");
}

$db = Database::getInstance();
$stmt = $db->prepare("SELECT user_id, movie_id FROM reviews WHERE id = ?");
$stmt->execute([$reviewId]);
$review = $stmt->fetch();

if (!$review) {
    Response::error("Yorum bulunamadı.", 404, "NOT_FOUND");
}

if ($review['user_id'] != $user['id'] && $user['role'] !== 'ADMIN') {
    Response::error("Bu yorumu silme yetkiniz yok.", 403, "FORBIDDEN");
}

$deleteStmt = $db->prepare("DELETE FROM reviews WHERE id = ?");
$deleteStmt->execute([$reviewId]);

require_once __DIR__ . '/../../../services/MovieService.php';
(new MovieService())->updateMovieStats($review['movie_id']);

Response::json(null, 200, "Yorum silindi.");
