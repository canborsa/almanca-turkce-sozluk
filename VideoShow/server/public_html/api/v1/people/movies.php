<?php
// VideoShow/server/public_html/api/v1/people/movies.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../includes/Database.php';

$personId = Validator::sanitizeInt($_GET['id'] ?? 0);
if (!$personId) {
    Response::error("Kişi ID'si gereklidir.", 400, "MISSING_ID");
}

$db = Database::getInstance();
$stmt = $db->prepare("
    SELECT m.*, mc.character_name, 'cast' as role_type
    FROM movies m
    INNER JOIN movie_cast mc ON m.id = mc.movie_id
    WHERE mc.person_id = ?
    UNION
    SELECT m.*, mc.job as character_name, 'crew' as role_type
    FROM movies m
    INNER JOIN movie_crew mc ON m.id = mc.movie_id
    WHERE mc.person_id = ?
    ORDER BY release_date DESC
");
$stmt->execute([$personId, $personId]);
$movies = $stmt->fetchAll();

Response::json($movies, 200);
