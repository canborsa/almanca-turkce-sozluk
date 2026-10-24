<?php
// VideoShow/server/public_html/admin/movies.php

require_once __DIR__ . '/../includes/Response.php';
require_once __DIR__ . '/../includes/Auth.php';
require_once __DIR__ . '/../includes/Pagination.php';
require_once __DIR__ . '/../services/MovieService.php';

$admin = Auth::requireAdmin();
$pagination = Pagination::getParams($_GET);

$movieService = new MovieService();
$result = $movieService->discoverMovies($_GET, $pagination['page'], $pagination['per_page']);

Response::json($result['movies'], 200, null, Pagination::format($result['total'], $pagination['page'], $pagination['per_page']));
