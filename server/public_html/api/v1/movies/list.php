<?php

require_once __DIR__ . '/../../../../includes/Response.php';
require_once __DIR__ . '/../../../../includes/Pagination.php';
require_once __DIR__ . '/../../../../services/MovieService.php';

$params = Pagination::getParams();
$movieService = new MovieService();

$result = $movieService->getList($params['page'], $params['per_page']);
$pagination = Pagination::makeMetaData($result['total'], $params['page'], $params['per_page']);

Response::json($result['items'], 200, null, $pagination);
