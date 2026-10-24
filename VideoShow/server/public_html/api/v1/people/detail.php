<?php
// VideoShow/server/public_html/api/v1/people/detail.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../includes/Database.php';

$id = Validator::sanitizeInt($_GET['id'] ?? 0);
if (!$id) {
    Response::error("Kişi ID'si gereklidir.", 400, "MISSING_ID");
}

$db = Database::getInstance();
$stmt = $db->prepare("SELECT * FROM people WHERE id = ? OR tmdb_id = ?");
$stmt->execute([$id, $id]);
$person = $stmt->fetch();

if (!$person) {
    Response::error("Kişi bulunamadı.", 404, "PERSON_NOT_FOUND");
}

$personId = $person['id'];

// Movies directed/written/produced
$stmt = $db->prepare("
    SELECT m.*, mc.job, mc.department
    FROM movies m
    INNER JOIN movie_crew mc ON m.id = mc.movie_id
    WHERE mc.person_id = ?
    ORDER BY m.release_date DESC
");
$stmt->execute([$personId]);
$person['crew_movies'] = $stmt->fetchAll();

// Movies acted in
$stmt = $db->prepare("
    SELECT m.*, mc.character_name, mc.cast_order
    FROM movies m
    INNER JOIN movie_cast mc ON m.id = mc.movie_id
    WHERE mc.person_id = ?
    ORDER BY m.release_date DESC
");
$stmt->execute([$personId]);
$person['cast_movies'] = $stmt->fetchAll();

Response::json($person, 200);
