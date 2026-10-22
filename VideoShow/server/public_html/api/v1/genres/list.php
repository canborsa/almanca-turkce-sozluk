<?php
// VideoShow/server/public_html/api/v1/genres/list.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Database.php';

$db = Database::getInstance();
$stmt = $db->query("SELECT * FROM genres ORDER BY name ASC");
$genres = $stmt->fetchAll();

Response::json($genres, 200);
