<?php
require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../services/RatingService.php';

$user = Auth::requireAuth();
$input = json_decode(file_get_contents('php://input'), true);

$movieId = (int)($input['movie_id'] ?? 0);
$rating = (float)($input['rating'] ?? 0.0);

if ($movieId <= 0 || $rating < 0.5 || $rating > 5.0) {
    Response::error('Geçersiz parametreler.');
}

$ratingService = new RatingService();
if ($ratingService->rateMovie($user['user_id'], $movieId, $rating)) {
    Response::json(null, true, 'Puan başarıyla kaydedildi.');
} else {
    Response::error('Puan kaydedilemedi.');
}
