<?php
// VideoShow/server/public_html/api/v1/auth/login.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../includes/RateLimiter.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../services/UserService.php';

$ip = $_SERVER['REMOTE_ADDR'] ?? '127.0.0.1';
if (!RateLimiter::check('login', $ip, 10, 300)) {
    Response::error("Çok fazla hatalı giriş denemesi. Lütfen 5 dakika sonra deneyin.", 429, "RATE_LIMIT_EXCEEDED");
}

$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;

$username = Validator::sanitizeString($input['username'] ?? '');
$password = $input['password'] ?? '';

if (empty($username) || empty($password)) {
    Response::error("Kullanıcı adı/e-posta ve şifre zorunludur.", 400, "VALIDATION_ERROR");
}

try {
    $userService = new UserService();
    $user = $userService->login($username, $password);
    $accessToken = Auth::generateToken($user, 86400);

    Response::json([
        'user' => $user,
        'access_token' => $accessToken,
        'token_type' => 'Bearer',
        'expires_in' => 86400
    ], 200, "Giriş başarılı.");
} catch (Exception $e) {
    Response::error($e->getMessage(), 401, "LOGIN_FAILED");
}
