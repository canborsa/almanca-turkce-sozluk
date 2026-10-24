<?php
// VideoShow/server/public_html/api/v1/lists/detail.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Validator.php';

$listId = Validator::sanitizeInt($_GET['id'] ?? 0);
if (!$listId) Response::error("Liste ID gereklidir.", 400, "MISSING_ID");

$db = Database::getInstance();
$stmt = $db->prepare("
    SELECT l.*, u.username, u.display_name, u.avatar_url
    FROM lists l
    INNER JOIN users u ON l.user_id = u.id
    WHERE l.id = ?
");
$stmt->execute([$listId]);
$list = $stmt->fetch();

if (!$list) Response::error("Liste bulunamadı.", 404, "NOT_FOUND");

$stmt = $db->prepare("
    SELECT m.*, lm.added_at
    FROM movies m
    INNER JOIN list_movies lm ON m.id = lm.movie_id
    WHERE lm.list_id = ?
    ORDER BY lm.sort_order ASC, lm.added_at DESC
");
$stmt->execute([$listId]);
$list['movies'] = $stmt->fetchAll();

Response::json($list, 200);
