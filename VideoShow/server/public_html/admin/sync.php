<?php
// VideoShow/server/public_html/admin/sync.php

require_once __DIR__ . '/../includes/Response.php';
require_once __DIR__ . '/../includes/Auth.php';
require_once __DIR__ . '/../includes/Database.php';

$admin = Auth::requireAdmin();
$db = Database::getInstance();

$stmt = $db->query("SELECT * FROM sync_logs ORDER BY id DESC LIMIT 20");
$logs = $stmt->fetchAll();

Response::json($logs, 200);
