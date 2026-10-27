<?php
require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../services/SearchService.php';

$q = trim($_GET['q'] ?? '');
if (empty($q)) {
    Response::json(['movies' => [], 'people' => []]);
}

$searchService = new SearchService();
$results = $searchService->search($q);
Response::json($results);
