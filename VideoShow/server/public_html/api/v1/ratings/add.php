<?php
// VideoShow/server/public_html/api/v1/ratings/add.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../services/RatingService.php';

$user = Auth::requireAuth();
$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;

$movieId = Validator::sanitizeInt($input['movie_id'] ?? 0);
$rating = Validator::sanitizeFloat($input['rating'] ?? 0);

if (!$movieId || $rating <= 0) {
    Response::error("Geçerli bir film ID ve puan belirtilmelidir.", 400, "VALIDATION_ERROR");
}

try {
    $ratingService = new RatingService();
    $result = $ratingService->setRating($user['id'], $movieId, $rating);
    Response::json($result, 200, "Puanınız kaydedildi.");
} catch (Exception $e) {
    Response::error($e->getMessage(), 400, "RATING_FAILED");
}
