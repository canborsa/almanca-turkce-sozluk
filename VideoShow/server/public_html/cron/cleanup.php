<?php
// VideoShow/server/public_html/cron/cleanup.php

require_once __DIR__ . '/../includes/Database.php';

$authConfig = require __DIR__ . '/../config/auth.php';
$secret = $_GET['secret'] ?? $_SERVER['HTTP_X_CRON_SECRET'] ?? null;

if (php_sapi_name() !== 'cli' && $secret !== $authConfig['cron_secret']) {
    http_response_code(403);
    exit("Forbidden");
}

// Clean old rate limit and log cache files older than 7 days
$cacheDir = __DIR__ . '/../storage/cache/rate_limits';
if (is_dir($cacheDir)) {
    foreach (glob("$cacheDir/*.json") as $file) {
        if (time() - filemtime($file) > 604800) {
            @unlink($file);
        }
    }
}

echo "Cleanup completed.\n";
