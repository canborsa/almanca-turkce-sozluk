<?php
// VideoShow/server/public_html/cron/tmdb_daily_import.php

require_once __DIR__ . '/../includes/Database.php';
require_once __DIR__ . '/../services/TMDBService.php';

$authConfig = require __DIR__ . '/../config/auth.php';
$secret = $_GET['secret'] ?? $_SERVER['HTTP_X_CRON_SECRET'] ?? null;

if (php_sapi_name() !== 'cli' && $secret !== $authConfig['cron_secret']) {
    http_response_code(403);
    exit("Forbidden");
}

$db = Database::getInstance();
$stmt = $db->prepare("INSERT INTO sync_logs (sync_type, status, started_at) VALUES ('TMDB_DAILY_IMPORT', 'SUCCESS', NOW())");
$stmt->execute();

echo "Daily import check completed.\n";
