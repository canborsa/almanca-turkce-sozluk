<?php

require_once __DIR__ . '/../../../../includes/Response.php';
require_once __DIR__ . '/../../../../includes/Validator.php';
require_once __DIR__ . '/../../../../services/UserService.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    Response::error('Yalnızca POST istekleri desteklenmektedir.', 405, 'METHOD_NOT_ALLOWED');
}

$input = Validator::getJsonInput();

$username = Validator::sanitizeString($input['username'] ?? '');
$email = Validator::sanitizeString($input['email'] ?? '');
$password = $input['password'] ?? '';
$displayName = Validator::sanitizeString($input['display_name'] ?? $username);

if (empty($username) || empty($email) || empty($password)) {
    Response::error('Lütfen tüm zorunlu alanları doldurun.', 400, 'VALIDATION_ERROR');
}

if (!Validator::isValidEmail($email)) {
    Response::error('Geçerli bir e-posta adresi girin.', 400, 'INVALID_EMAIL');
}

try {
    $userService = new UserService();
    $result = $userService->register($username, $email, $password, $displayName);
    Response::json($result, 201, 'Kayıt başarılı.');
} catch (Exception $e) {
    Response::error($e->getMessage(), 400, 'REGISTER_FAILED');
}
