<?php
// VideoShow/server/public_html/cron/update_statistics.php

require_once __DIR__ . '/../includes/Database.php';
require_once __DIR__ . '/../services/MovieService.php';

$authConfig = require __DIR__ . '/../config/auth.php';
$secret = $_GET['secret'] ?? $_SERVER['HTTP_X_CRON_SECRET'] ?? null;

if (php_sapi_name() !== 'cli' && $secret !== $authConfig['cron_secret']) {
    http_response_code(403);
    exit("Forbidden");
}

$db = Database::getInstance();
$movieService = new MovieService();

$stmt = $db->query("SELECT id FROM movies");
$movieIds = $stmt->fetchAll(PDO::FETCH_COLUMN);

foreach ($movieIds as $id) {
    $movieService->updateMovieStats((int)$id);
}

echo "Statistics updated for " . count($movieIds) . " movies.\n";
