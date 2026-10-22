<?php
// VideoShow/server/public_html/api/v1/discovery/filters.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Database.php';

$db = Database::getInstance();
$stmt = $db->query("SELECT id, tmdb_id, name FROM genres ORDER BY name ASC");
$genres = $stmt->fetchAll();

$filters = [
    'genres' => $genres,
    'year_range' => ['min' => 1900, 'max' => (int)date('Y') + 2],
    'rating_range' => ['min' => 0.0, 'max' => 10.0],
    'sort_options' => [
        ['key' => 'popularity', 'label' => 'En Popüler'],
        ['key' => 'vote_average', 'label' => 'En Yüksek Puanlı (TMDB)'],
        ['key' => 'weighted_rating', 'label' => 'En Yüksek Puanlı (Uygulama)'],
        ['key' => 'release_date', 'label' => 'En Yeni'],
        ['key' => 'vote_count', 'label' => 'En Çok Oy Alan'],
        ['key' => 'title', 'label' => 'Alfabetik']
    ]
];

Response::json($filters, 200);
