<?php

require_once __DIR__ . '/../../../../includes/Response.php';
require_once __DIR__ . '/../../../../includes/Validator.php';
require_once __DIR__ . '/../../../../includes/Pagination.php';
require_once __DIR__ . '/../../../../services/MovieService.php';

$q = Validator::sanitizeString($_GET['q'] ?? '');

if (empty($q)) {
    Response::error('Arama terimi boş olamaz.', 400, 'EMPTY_QUERY');
}

$params = Pagination::getParams();
$movieService = new MovieService();

$result = $movieService->search($q, $params['page'], $params['per_page']);
$pagination = Pagination::makeMetaData($result['total'], $params['page'], $params['per_page']);

Response::json($result['items'], 200, null, $pagination);
