<?php
// VideoShow/server/public_html/api/v1/reviews/create.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../includes/RateLimiter.php';
require_once __DIR__ . '/../../../services/ReviewService.php';

$user = Auth::requireAuth();

if (!RateLimiter::check('review', (string)$user['id'], 15, 60)) {
    Response::error("Çok hızlı yorum gönderiyorsunuz. Lütfen biraz bekleyin.", 429, "RATE_LIMIT_EXCEEDED");
}

$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;

$movieId = Validator::sanitizeInt($input['movie_id'] ?? 0);
$body = Validator::sanitizeString($input['body'] ?? '');
$title = Validator::sanitizeString($input['title'] ?? '');
$spoiler = (bool)($input['spoiler'] ?? false);

if (!$movieId || empty($body)) {
    Response::error("Film ID ve yorum metni zorunludur.", 400, "VALIDATION_ERROR");
}

try {
    $reviewService = new ReviewService();
    $review = $reviewService->createReview($user['id'], $movieId, $body, $title, $spoiler);
    Response::json($review, 201, "Yorumunuz kaydedildi.");
} catch (Exception $e) {
    Response::error($e->getMessage(), 400, "REVIEW_CREATE_FAILED");
}
