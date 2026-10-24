<?php
// VideoShow/server/public_html/admin/index.php

require_once __DIR__ . '/../includes/Response.php';
require_once __DIR__ . '/../includes/Auth.php';
require_once __DIR__ . '/../includes/Database.php';

$admin = Auth::requireAdmin();
$db = Database::getInstance();

$stats = [
    'total_movies'    => (int)$db->query("SELECT COUNT(*) FROM movies")->fetchColumn(),
    'total_users'     => (int)$db->query("SELECT COUNT(*) FROM users")->fetchColumn(),
    'total_reviews'   => (int)$db->query("SELECT COUNT(*) FROM reviews")->fetchColumn(),
    'total_ratings'   => (int)$db->query("SELECT COUNT(*) FROM ratings")->fetchColumn(),
    'total_favorites' => (int)$db->query("SELECT COUNT(*) FROM favorites")->fetchColumn(),
    'total_watchlist' => (int)$db->query("SELECT COUNT(*) FROM watchlist")->fetchColumn(),
    'total_reports'   => (int)$db->query("SELECT COUNT(*) FROM reports WHERE status = 'PENDING'")->fetchColumn(),
    'last_sync'       => $db->query("SELECT * FROM sync_logs ORDER BY id DESC LIMIT 1")->fetch() ?: null
];

Response::json($stats, 200);
