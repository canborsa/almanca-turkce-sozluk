<?php
require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Database.php';

$db = Database::getInstance();
$genres = $db->query("SELECT * FROM genres ORDER BY name ASC")->fetchAll();
Response::json($genres);
