<?php
// VideoShow/server/public_html/admin/reviews.php

require_once __DIR__ . '/../includes/Response.php';
require_once __DIR__ . '/../includes/Auth.php';
require_once __DIR__ . '/../includes/Database.php';

$admin = Auth::requireAdmin();
$db = Database::getInstance();

$stmt = $db->query("
    SELECT r.*, u.username, m.title as movie_title
    FROM reviews r
    INNER JOIN users u ON r.user_id = u.id
    INNER JOIN movies m ON r.movie_id = m.id
    ORDER BY r.created_at DESC LIMIT 50
");
$reviews = $stmt->fetchAll();

Response::json($reviews, 200);
