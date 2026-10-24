<?php
// VideoShow/server/public_html/api/v1/auth/register.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../includes/RateLimiter.php';
require_once __DIR__ . '/../../../includes/Validator.php';
require_once __DIR__ . '/../../../services/UserService.php';

$ip = $_SERVER['REMOTE_ADDR'] ?? '127.0.0.1';
if (!RateLimiter::check('register', $ip, 5, 600)) {
    Response::error("Çok fazla kayıt denemesi yaptınız. Lütfen daha sonra tekrar deneyin.", 429, "RATE_LIMIT_EXCEEDED");
}

$input = json_decode(file_get_contents('php://input'), true) ?? $_POST;

$username = Validator::sanitizeString($input['username'] ?? '');
$email = Validator::sanitizeString($input['email'] ?? '');
$password = $input['password'] ?? '';
$displayName = Validator::sanitizeString($input['display_name'] ?? '');

if (empty($username) || empty($email) || empty($password)) {
    Response::error("Kullanıcı adı, e-posta ve şifre alanları zorunludur.", 400, "VALIDATION_ERROR");
}

if (!Validator::isValidEmail($email)) {
    Response::error("Geçersiz e-posta adresi.", 400, "INVALID_EMAIL");
}

try {
    $userService = new UserService();
    $user = $userService->register($username, $email, $password, $displayName);
    $accessToken = Auth::generateToken($user, 86400);

    Response::json([
        'user' => $user,
        'access_token' => $accessToken,
        'token_type' => 'Bearer',
        'expires_in' => 86400
    ], 201, "Kayıt başarıyla tamamlandı.");
} catch (Exception $e) {
    Response::error($e->getMessage(), 400, "REGISTER_FAILED");
}
