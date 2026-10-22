<?php
// VideoShow/server/public_html/api/v1/genres/movies.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../includes/Pagination.php';
require_once __DIR__ . '/../../../services/MovieService.php';

$genreId = Validator::sanitizeInt($_GET['id'] ?? 0);
if (!$genreId) {
    Response::error("Tür ID'si gereklidir.", 400, "MISSING_ID");
}

$pagination = Pagination::getParams($_GET);
$movieService = new MovieService();
$filters = array_merge($_GET, ['genres' => (string)$genreId]);
$result = $movieService->discoverMovies($filters, $pagination['page'], $pagination['per_page']);

Response::json(
    $result['movies'],
    200,
    null,
    Pagination::format($result['total'], $pagination['page'], $pagination['per_page'])
);
