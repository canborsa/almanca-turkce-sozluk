<?php
// VideoShow/server/public_html/api/v1/auth/refresh.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../services/UserService.php';

$user = Auth::requireAuth();

try {
    $userService = new UserService();
    $userData = $userService->getUserById($user['id']);
    $newToken = Auth::generateToken($userData, 86400);

    Response::json([
        'user' => $userData,
        'access_token' => $newToken,
        'token_type' => 'Bearer',
        'expires_in' => 86400
    ], 200, "Token yenilendi.");
} catch (Exception $e) {
    Response::error("Token yenilenemedi.", 401, "REFRESH_FAILED");
}
