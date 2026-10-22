<?php
// VideoShow/server/public_html/cron/tmdb_people_update.php

require_once __DIR__ . '/../includes/Database.php';

$authConfig = require __DIR__ . '/../config/auth.php';
$secret = $_GET['secret'] ?? $_SERVER['HTTP_X_CRON_SECRET'] ?? null;

if (php_sapi_name() !== 'cli' && $secret !== $authConfig['cron_secret']) {
    http_response_code(403);
    exit("Forbidden");
}

echo "People update cron completed.\n";
