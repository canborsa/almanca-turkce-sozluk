<?php
require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../services/ReviewService.php';

$user = Auth::requireAuth();
$input = json_decode(file_get_contents('php://input'), true);

$movieId = (int)($input['movie_id'] ?? 0);
$title = trim($input['title'] ?? '');
$body = trim($input['body'] ?? '');
$spoiler = (bool)($input['spoiler'] ?? false);

if ($movieId <= 0 || empty($body)) {
    Response::error('Lütfen yorum içeriğini girin.');
}

$reviewService = new ReviewService();
if ($reviewService->createReview($user['user_id'], $movieId, $body, $title, $spoiler)) {
    Response::json(null, true, 'Yorumunuz başarıyla eklendi.');
} else {
    Response::error('Yorum eklenirken bir hata oluştu.');
}
