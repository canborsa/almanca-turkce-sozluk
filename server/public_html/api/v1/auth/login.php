<?php

require_once __DIR__ . '/../../../../includes/Response.php';
require_once __DIR__ . '/../../../../includes/Validator.php';
require_once __DIR__ . '/../../../../services/UserService.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    Response::error('Yalnızca POST istekleri desteklenmektedir.', 405, 'METHOD_NOT_ALLOWED');
}

$input = Validator::getJsonInput();

$email = Validator::sanitizeString($input['email'] ?? '');
$password = $input['password'] ?? '';

if (empty($email) || empty($password)) {
    Response::error('E-posta ve şifre zorunludur.', 400, 'VALIDATION_ERROR');
}

try {
    $userService = new UserService();
    $result = $userService->login($email, $password);
    Response::json($result, 200, 'Giriş başarılı.');
} catch (Exception $e) {
    Response::error($e->getMessage(), 401, 'LOGIN_FAILED');
}
