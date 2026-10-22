<?php
// VideoShow/server/public_html/api/v1/watched/list.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../includes/Pagination.php';

$user = Auth::requireAuth();
$pagination = Pagination::getParams($_GET);

$db = Database::getInstance();
$countStmt = $db->prepare("SELECT COUNT(*) FROM watched WHERE user_id = ?");
$countStmt->execute([$user['id']]);
$total = (int)$countStmt->fetchColumn();

$stmt = $db->prepare("
    SELECT m.*, w.watched_at
    FROM movies m
    INNER JOIN watched w ON m.id = w.movie_id
    WHERE w.user_id = ?
    ORDER BY w.watched_at DESC
    LIMIT ? OFFSET ?
");
$stmt->execute([$user['id'], $pagination['per_page'], $pagination['offset']]);
$movies = $stmt->fetchAll();

Response::json(
    $movies,
    200,
    null,
    Pagination::format($total, $pagination['page'], $pagination['per_page'])
);
