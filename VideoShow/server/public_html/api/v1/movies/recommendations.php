<?php
// VideoShow/server/public_html/api/v1/movies/recommendations.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../services/RecommendationService.php';

$user = Auth::requireAuth();
$recommendationService = new RecommendationService();
$movies = $recommendationService->getPersonalizedRecommendations($user['id'], 20);

Response::json($movies, 200);
