<?php
// VideoShow/server/public_html/api/v1/lists/create.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../includes/Validator.php';

$user = Auth::requireAuth();
$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;

$title = Validator::sanitizeString($input['title'] ?? '');
$description = Validator::sanitizeString($input['description'] ?? '');
$isPublic = !isset($input['is_public']) || (bool)$input['is_public'];

if (empty($title)) Response::error("Liste başlığı gereklidir.", 400, "VALIDATION_ERROR");

$db = Database::getInstance();
$stmt = $db->prepare("INSERT INTO lists (user_id, title, description, is_public) VALUES (?, ?, ?, ?)");
$stmt->execute([$user['id'], $title, $description, $isPublic ? 1 : 0]);
$listId = (int)$db->lastInsertId();

Response::json(['id' => $listId, 'title' => $title], 201, "Liste oluşturuldu.");
