<?php
// VideoShow/server/public_html/api/v1/lists/update.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../includes/Validator.php';

$user = Auth::requireAuth();
$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;

$listId = Validator::sanitizeInt($input['id'] ?? 0);
$title = Validator::sanitizeString($input['title'] ?? '');
$description = Validator::sanitizeString($input['description'] ?? '');
$isPublic = !isset($input['is_public']) || (bool)$input['is_public'];

if (!$listId || empty($title)) Response::error("Liste ID ve Başlık gereklidir.", 400, "VALIDATION_ERROR");

$db = Database::getInstance();
$stmt = $db->prepare("SELECT user_id FROM lists WHERE id = ?");
$stmt->execute([$listId]);
if ($stmt->fetchColumn() != $user['id'] && $user['role'] !== 'ADMIN') {
    Response::error("Bu listeyi düzenleme yetkiniz yok.", 403, "FORBIDDEN");
}

$updateStmt = $db->prepare("UPDATE lists SET title = ?, description = ?, is_public = ? WHERE id = ?");
$updateStmt->execute([$title, $description, $isPublic ? 1 : 0, $listId]);

Response::json(null, 200, "Liste güncellendi.");
