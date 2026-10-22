<?php
// VideoShow/server/public_html/api/v1/lists/delete.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../includes/Validator.php';

$user = Auth::requireAuth();
$input = json_decode(file_get_contents('php://input'), true) ?? $_GET;
$listId = Validator::sanitizeInt($input['id'] ?? 0);

if (!$listId) Response::error("Liste ID gereklidir.", 400, "MISSING_ID");

$db = Database::getInstance();
$stmt = $db->prepare("SELECT user_id FROM lists WHERE id = ?");
$stmt->execute([$listId]);
if ($stmt->fetchColumn() != $user['id'] && $user['role'] !== 'ADMIN') {
    Response::error("Bu listeyi silme yetkiniz yok.", 403, "FORBIDDEN");
}

$deleteStmt = $db->prepare("DELETE FROM lists WHERE id = ?");
$deleteStmt->execute([$listId]);

Response::json(null, 200, "Liste silindi.");
