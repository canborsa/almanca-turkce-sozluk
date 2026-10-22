<?php
// VideoShow/server/public_html/admin/reports.php

require_once __DIR__ . '/../includes/Response.php';
require_once __DIR__ . '/../includes/Auth.php';
require_once __DIR__ . '/../includes/Database.php';

$admin = Auth::requireAdmin();
$db = Database::getInstance();

$stmt = $db->query("
    SELECT rep.*, u.username as reporter, r.body as review_body, r.movie_id
    FROM reports rep
    INNER JOIN users u ON rep.user_id = u.id
    INNER JOIN reviews r ON rep.review_id = r.id
    ORDER BY rep.created_at DESC
");
$reports = $stmt->fetchAll();

Response::json($reports, 200);
