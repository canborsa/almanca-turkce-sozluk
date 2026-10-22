<?php
// VideoShow/server/public_html/api/v1/ratings/delete.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../services/RatingService.php';

$user = Auth::requireAuth();
$input = json_decode(file_get_contents('php://input'), true) ?? $_GET;

$movieId = Validator::sanitizeInt($input['movie_id'] ?? 0);
if (!$movieId) {
    Response::error("Film ID gereklidir.", 400, "MISSING_ID");
}

try {
    $ratingService = new RatingService();
    $ratingService->deleteRating($user['id'], $movieId);
    Response::json(null, 200, "Puanınız silindi.");
} catch (Exception $e) {
    Response::error($e->getMessage(), 400, "DELETE_FAILED");
}
