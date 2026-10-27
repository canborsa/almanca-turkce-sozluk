<?php
require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Database.php';
require_once __DIR__ . '/../../../includes/Pagination.php';

$db = Database::getInstance();
$pagination = Pagination::getParams($_GET);

$sql = "SELECT m.* FROM movies m WHERE 1=1";
$params = [];

if (!empty($_GET['genres'])) {
    $genreIds = array_map('intval', explode(',', $_GET['genres']));
    if (!empty($genreIds)) {
        $in = implode(',', $genreIds);
        $sql .= " AND m.id IN (SELECT movie_id FROM movie_genres WHERE genre_id IN ($in))";
    }
}

if (!empty($_GET['year_min'])) {
    $sql .= " AND m.release_year >= :year_min";
    $params[':year_min'] = (int)$_GET['year_min'];
}

if (!empty($_GET['year_max'])) {
    $sql .= " AND m.release_year <= :year_max";
    $params[':year_max'] = (int)$_GET['year_max'];
}

if (!empty($_GET['rating_min'])) {
    $sql .= " AND (m.app_vote_average >= :rating_min OR m.tmdb_vote_average >= :rating_min)";
    $params[':rating_min'] = (float)$_GET['rating_min'];
}

if (!empty($_GET['votes_min'])) {
    $sql .= " AND (m.app_vote_count >= :votes_min OR m.tmdb_vote_count >= :votes_min)";
    $params[':votes_min'] = (int)$_GET['votes_min'];
}

if (!empty($_GET['duration_min'])) {
    $sql .= " AND m.runtime >= :duration_min";
    $params[':duration_min'] = (int)$_GET['duration_min'];
}

if (!empty($_GET['duration_max'])) {
    $sql .= " AND m.runtime <= :duration_max";
    $params[':duration_max'] = (int)$_GET['duration_max'];
}

$sortBy = $_GET['sort_by'] ?? 'popularity';
switch ($sortBy) {
    case 'vote_average':
        $sql .= " ORDER BY m.app_vote_average DESC, m.tmdb_vote_average DESC";
        break;
    case 'release_date':
        $sql .= " ORDER BY m.release_date DESC";
        break;
    default:
        $sql .= " ORDER BY m.popularity DESC";
        break;
}

$sql .= " LIMIT " . $pagination['per_page'] . " OFFSET " . $pagination['offset'];

$stmt = $db->prepare($sql);
$stmt->execute($params);
$movies = $stmt->fetchAll();

Response::json($movies, true, null, 200, Pagination::format(count($movies), $pagination['page'], $pagination['per_page']));
