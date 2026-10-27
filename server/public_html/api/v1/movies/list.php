<?php
require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Pagination.php';
require_once __DIR__ . '/../../../services/MovieService.php';

$pagination = Pagination::getParams($_GET);
$movieService = new MovieService();

$movies = $movieService->getMovies($pagination['per_page'], $pagination['offset']);
Response::json($movies, true, null, 200, Pagination::format(count($movies), $pagination['page'], $pagination['per_page']));
