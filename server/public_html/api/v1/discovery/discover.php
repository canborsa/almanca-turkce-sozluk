<?php

require_once __DIR__ . '/../../../../includes/Response.php';
require_once __DIR__ . '/../../../../includes/Validator.php';
require_once __DIR__ . '/../../../../includes/Pagination.php';
require_once __DIR__ . '/../../../../services/MovieService.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    Response::error('Yalnızca POST istekleri desteklenmektedir.', 405, 'METHOD_NOT_ALLOWED');
}

$filters = Validator::getJsonInput();
$params = Pagination::getParams();

$movieService = new MovieService();
$result = $movieService->discover($filters, $params['page'], $params['per_page']);
$pagination = Pagination::makeMetaData($result['total'], $params['page'], $params['per_page']);

Response::json($result['items'], 200, null, $pagination);
