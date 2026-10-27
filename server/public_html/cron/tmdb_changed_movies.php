<?php
require_once __DIR__ . '/../includes/Logger.php';

$config = require __DIR__ . '/../config/app.php';
$secret = $_GET['secret'] ?? $_SERVER['HTTP_X_CRON_SECRET'] ?? null;

if (php_sapi_name() !== 'cli' && $secret !== $config['cron_secret']) {
    http_response_code(403);
    echo "Cron yetkisiz erişim.";
    exit;
}

Logger::log('CRON', 'tmdb_changed_movies.php executed successfully.');
echo "Changed Movies Sync Completed.\n";
