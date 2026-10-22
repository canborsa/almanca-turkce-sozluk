<?php
// VideoShow/server/public_html/api/v1/people/search.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../services/SearchService.php';

$query = Validator::sanitizeString($_GET['q'] ?? $_GET['query'] ?? '');
if (empty($query)) {
    Response::json([], 200);
}

$searchService = new SearchService();
$result = $searchService->search($query);

Response::json($result['people'], 200);
