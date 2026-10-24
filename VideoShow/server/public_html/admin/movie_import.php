<?php
// VideoShow/server/public_html/admin/movie_import.php

require_once __DIR__ . '/../includes/Response.php';
require_once __DIR__ . '/../includes/Auth.php';
require_once __DIR__ . '/../includes/Validator.php';
require_once __DIR__ . '/../includes/Database.php';
require_once __DIR__ . '/../services/TMDBService.php';

$admin = Auth::requireAdmin();
$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;

$tmdbId = Validator::sanitizeInt($input['tmdb_id'] ?? $_GET['tmdb_id'] ?? 0);
if (!$tmdbId) {
    Response::error("TMDB ID gereklidir.", 400, "MISSING_TMDB_ID");
}

$tmdbService = new TMDBService();
$details = $tmdbService->getMovieDetails($tmdbId);

if (!$details || empty($details['id'])) {
    Response::error("TMDB'den film bilgisi alınamadı.", 404, "TMDB_FETCH_FAILED");
}

$db = Database::getInstance();

try {
    $db->beginTransaction();

    $releaseYear = !empty($details['release_date']) ? (int)substr($details['release_date'], 0, 4) : null;
    $driver = $db->getAttribute(PDO::ATTR_DRIVER_NAME);

    if ($driver === 'sqlite') {
        $stmt = $db->prepare("
            INSERT INTO movies (
                tmdb_id, imdb_id, title, original_title, overview, tagline, release_date, release_year,
                runtime, poster_path, backdrop_path, original_language, status, adult, vote_average,
                vote_count, popularity, budget, revenue, homepage, tmdb_updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
            ON CONFLICT(tmdb_id) DO UPDATE SET
                title = excluded.title,
                overview = excluded.overview,
                tagline = excluded.tagline,
                release_date = excluded.release_date,
                release_year = excluded.release_year,
                runtime = excluded.runtime,
                poster_path = excluded.poster_path,
                backdrop_path = excluded.backdrop_path,
                vote_average = excluded.vote_average,
                vote_count = excluded.vote_count,
                popularity = excluded.popularity,
                tmdb_updated_at = CURRENT_TIMESTAMP
        ");
    } else {
        $stmt = $db->prepare("
            INSERT INTO movies (
                tmdb_id, imdb_id, title, original_title, overview, tagline, release_date, release_year,
                runtime, poster_path, backdrop_path, original_language, status, adult, vote_average,
                vote_count, popularity, budget, revenue, homepage, tmdb_updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
            ON DUPLICATE KEY UPDATE
                title = VALUES(title),
                overview = VALUES(overview),
                tagline = VALUES(tagline),
                release_date = VALUES(release_date),
                release_year = VALUES(release_year),
                runtime = VALUES(runtime),
                poster_path = VALUES(poster_path),
                backdrop_path = VALUES(backdrop_path),
                vote_average = VALUES(vote_average),
                vote_count = VALUES(vote_count),
                popularity = VALUES(popularity),
                tmdb_updated_at = NOW()
        ");
    }

    $stmt->execute([
        $details['id'],
        $details['imdb_id'] ?? null,
        $details['title'],
        $details['original_title'] ?? null,
        $details['overview'] ?? null,
        $details['tagline'] ?? null,
        $details['release_date'] ?: null,
        $releaseYear,
        $details['runtime'] ?? null,
        $details['poster_path'] ?? null,
        $details['backdrop_path'] ?? null,
        $details['original_language'] ?? 'en',
        $details['status'] ?? 'Released',
        !empty($details['adult']) ? 1 : 0,
        $details['vote_average'] ?? 0.0,
        $details['vote_count'] ?? 0,
        $details['popularity'] ?? 0.0,
        $details['budget'] ?? 0,
        $details['revenue'] ?? 0,
        $details['homepage'] ?? null
    ]);

    // Get inserted/updated movie ID
    $stmt = $db->prepare("SELECT id FROM movies WHERE tmdb_id = ?");
    $stmt->execute([$details['id']]);
    $movieId = (int)$stmt->fetchColumn();

    // Insert query according to database driver
    $ignoreClause = ($driver === 'sqlite') ? 'INSERT OR IGNORE INTO' : 'INSERT IGNORE INTO';

    // Genres
    if (!empty($details['genres'])) {
        foreach ($details['genres'] as $g) {
            if ($driver === 'sqlite') {
                $gStmt = $db->prepare("INSERT INTO genres (tmdb_id, name) VALUES (?, ?) ON CONFLICT(tmdb_id) DO UPDATE SET name=excluded.name");
            } else {
                $gStmt = $db->prepare("INSERT INTO genres (tmdb_id, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name)");
            }
            $gStmt->execute([$g['id'], $g['name']]);

            $stmtG = $db->prepare("SELECT id FROM genres WHERE tmdb_id = ?");
            $stmtG->execute([$g['id']]);
            $genreDbId = (int)$stmtG->fetchColumn();

            $mgStmt = $db->prepare("$ignoreClause movie_genres (movie_id, genre_id) VALUES (?, ?)");
            $mgStmt->execute([$movieId, $genreDbId]);
        }
    }

    // Cast & Crew
    if (!empty($details['credits']['cast'])) {
        foreach (array_slice($details['credits']['cast'], 0, 15) as $cast) {
            if ($driver === 'sqlite') {
                $pStmt = $db->prepare("INSERT INTO people (tmdb_id, name, profile_path) VALUES (?, ?, ?) ON CONFLICT(tmdb_id) DO UPDATE SET name=excluded.name, profile_path=excluded.profile_path");
            } else {
                $pStmt = $db->prepare("INSERT INTO people (tmdb_id, name, profile_path) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), profile_path=VALUES(profile_path)");
            }
            $pStmt->execute([$cast['id'], $cast['name'], $cast['profile_path'] ?? null]);

            $pDbStmt = $db->prepare("SELECT id FROM people WHERE tmdb_id = ?");
            $pDbStmt->execute([$cast['id']]);
            $personDbId = (int)$pDbStmt->fetchColumn();

            $mcStmt = $db->prepare("$ignoreClause movie_cast (movie_id, person_id, character_name, cast_order) VALUES (?, ?, ?, ?)");
            $mcStmt->execute([$movieId, $personDbId, $cast['character'] ?? null, $cast['order'] ?? 0]);
        }
    }

    $db->commit();

    Response::json(['movie_id' => $movieId, 'tmdb_id' => $details['id'], 'title' => $details['title']], 200, "Film başarıyla aktarıldı.");
} catch (Exception $e) {
    if ($db->inTransaction()) $db->rollBack();
    Response::error("Film aktarılırken hata oluştu: " . $e->getMessage(), 500, "IMPORT_FAILED");
}
