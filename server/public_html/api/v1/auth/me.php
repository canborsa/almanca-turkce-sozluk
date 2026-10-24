<?php

require_once __DIR__ . '/../../../../includes/Response.php';
require_once __DIR__ . '/../../../../includes/Auth.php';
require_once __DIR__ . '/../../../../services/UserService.php';

$userData = Auth::requireAuth();

try {
    $userService = new UserService();
    $profile = $userService->getProfile((int)$userData['user_id']);
    Response::json(['user' => $profile], 200);
} catch (Exception $e) {
    Response::error($e->getMessage(), 404, 'USER_NOT_FOUND');
}
