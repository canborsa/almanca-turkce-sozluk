<?php
// VideoShow/server/public_html/api/v1/movies/trending.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Pagination.php';
require_once __DIR__ . '/../../../services/MovieService.php';

$pagination = Pagination::getParams($_GET);
$movieService = new MovieService();
$filters = array_merge($_GET, ['sort_by' => 'popularity']);
$result = $movieService->discoverMovies($filters, $pagination['page'], $pagination['per_page']);

Response::json(
    $result['movies'],
    200,
    null,
    Pagination::format($result['total'], $pagination['page'], $pagination['per_page'])
);
