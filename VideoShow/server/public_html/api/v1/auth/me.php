<?php
// VideoShow/server/public_html/api/v1/auth/me.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../services/UserService.php';

$authUser = Auth::requireAuth();

try {
    $userService = new UserService();
    $userStats = $userService->getUserProfileStats($authUser['id']);
    Response::json($userStats, 200);
} catch (Exception $e) {
    Response::error($e->getMessage(), 404, "USER_NOT_FOUND");
}
