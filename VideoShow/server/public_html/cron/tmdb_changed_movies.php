<?php
// VideoShow/server/public_html/cron/tmdb_changed_movies.php

require_once __DIR__ . '/../includes/Database.php';
require_once __DIR__ . '/../includes/Logger.php';
require_once __DIR__ . '/../services/TMDBService.php';

$authConfig = require __DIR__ . '/../config/auth.php';
$secret = $_GET['secret'] ?? $_SERVER['HTTP_X_CRON_SECRET'] ?? null;

if (php_sapi_name() !== 'cli' && $secret !== $authConfig['cron_secret']) {
    http_response_code(403);
    echo "Forbidden";
    exit;
}

$db = Database::getInstance();
$startedAt = date('Y-m-d H:i:s');

$stmt = $db->prepare("INSERT INTO sync_logs (sync_type, status, started_at) VALUES ('TMDB_CHANGED_MOVIES', 'RUNNING', ?)");
$stmt->execute([$startedAt]);
$logId = (int)$db->lastInsertId();

$tmdbService = new TMDBService();
$endDate = date('Y-m-d');
$startDate = date('Y-m-d', strtotime('-1 day'));

$changes = $tmdbService->getMovieChanges($startDate, $endDate);
$processed = 0;
$updated = 0;

if (!empty($changes['results'])) {
    foreach ($changes['results'] as $item) {
        $tmdbId = $item['id'];
        $processed++;

        // Check if exists in our DB
        $checkStmt = $db->prepare("SELECT id FROM movies WHERE tmdb_id = ?");
        $checkStmt->execute([$tmdbId]);
        if ($checkStmt->fetch()) {
            // Re-fetch details and update
            $details = $tmdbService->getMovieDetails($tmdbId);
            if ($details) {
                $upStmt = $db->prepare("
                    UPDATE movies SET
                        title = ?, overview = ?, tagline = ?, release_date = ?,
                        vote_average = ?, vote_count = ?, popularity = ?, tmdb_updated_at = NOW()
                    WHERE tmdb_id = ?
                ");
                $upStmt->execute([
                    $details['title'], $details['overview'] ?? null, $details['tagline'] ?? null,
                    $details['release_date'] ?: null, $details['vote_average'] ?? 0.0,
                    $details['vote_count'] ?? 0, $details['popularity'] ?? 0.0, $tmdbId
                ]);
                $updated++;
            }
        }
    }
}

$finishedAt = date('Y-m-d H:i:s');
$updateLog = $db->prepare("
    UPDATE sync_logs SET status = 'SUCCESS', finished_at = ?, processed = ?, updated = ? WHERE id = ?
");
$updateLog->execute([$finishedAt, $processed, $updated, $logId]);

echo "Sync completed. Processed: $processed, Updated: $updated\n";
