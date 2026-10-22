<?php
// VideoShow/server/public_html/api/v1/reviews/update.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../includes/Database.php';

$user = Auth::requireAuth();
$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;

$reviewId = Validator::sanitizeInt($input['id'] ?? 0);
$body = Validator::sanitizeString($input['body'] ?? '');
$title = Validator::sanitizeString($input['title'] ?? '');
$spoiler = (bool)($input['spoiler'] ?? false);

if (!$reviewId || empty($body)) {
    Response::error("Yorum ID ve yorum metni zorunludur.", 400, "VALIDATION_ERROR");
}

$db = Database::getInstance();
$stmt = $db->prepare("SELECT user_id FROM reviews WHERE id = ?");
$stmt->execute([$reviewId]);
$review = $stmt->fetch();

if (!$review) {
    Response::error("Yorum bulunamadı.", 404, "NOT_FOUND");
}

if ($review['user_id'] != $user['id'] && $user['role'] !== 'ADMIN') {
    Response::error("Bu yorumu düzenleme yetkiniz yok.", 433, "FORBIDDEN");
}

$updateStmt = $db->prepare("UPDATE reviews SET body = ?, title = ?, spoiler = ? WHERE id = ?");
$updateStmt->execute([$body, $title, $spoiler ? 1 : 0, $reviewId]);

Response::json(null, 200, "Yorum güncellendi.");
