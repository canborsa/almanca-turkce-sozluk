<?php
// VideoShow/server/public_html/api/v1/reviews/list.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../includes/Pagination.php';
require_once __DIR__ . '/../../../services/ReviewService.php';

$movieId = Validator::sanitizeInt($_GET['movie_id'] ?? 0);
if (!$movieId) {
    Response::error("Film ID gereklidir.", 400, "MISSING_ID");
}

$pagination = Pagination::getParams($_GET);
$sort = Validator::sanitizeString($_GET['sort'] ?? 'newest');

$reviewService = new ReviewService();
$result = $reviewService->getMovieReviews($movieId, $pagination['page'], $pagination['per_page'], $sort);

Response::json(
    $result['reviews'],
    200,
    null,
    Pagination::format($result['total'], $pagination['page'], $pagination['per_page'])
);
