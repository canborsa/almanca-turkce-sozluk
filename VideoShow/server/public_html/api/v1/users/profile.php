<?php
// VideoShow/server/public_html/api/v1/users/profile.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../services/UserService.php';

$userId = Validator::sanitizeInt($_GET['id'] ?? 0);
if (!$userId) Response::error("Kullanıcı ID gereklidir.", 400, "MISSING_ID");

try {
    $userService = new UserService();
    $profile = $userService->getUserProfileStats($userId);
    Response::json($profile, 200);
} catch (Exception $e) {
    Response::error($e->getMessage(), 404, "NOT_FOUND");
}
