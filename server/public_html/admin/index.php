<?php
require_once __DIR__ . '/../includes/Response.php';
require_once __DIR__ . '/../includes/Auth.php';
require_once __DIR__ . '/../includes/Database.php';

$user = Auth::requireAdmin();
$db = Database::getInstance();

$totalMovies = $db->query("SELECT COUNT(*) FROM movies")->fetchColumn();
$totalUsers = $db->query("SELECT COUNT(*) FROM users")->fetchColumn();
$totalReviews = $db->query("SELECT COUNT(*) FROM reviews")->fetchColumn();

Response::json([
    'total_movies' => $totalMovies,
    'total_users' => $totalUsers,
    'total_reviews' => $totalReviews,
    'last_sync' => date('Y-m-d H:i:s')
]);
