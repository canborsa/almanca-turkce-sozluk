<?php
require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';
require_once __DIR__ . '/../../../services/UserService.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    Response::error('Sadece POST metodu desteklenmektedir.', 405);
}

$input = json_decode(file_get_contents('php://input'), true);
$email = trim($input['email'] ?? '');
$password = trim($input['password'] ?? '');

if (empty($email) || empty($password)) {
    Response::error('E-posta ve şifre gereklidir.');
}

$userService = new UserService();
$user = $userService->login($email, $password);

if ($user) {
    $token = Auth::generateToken([
        'user_id' => $user['id'],
        'username' => $user['username'],
        'email' => $user['email'],
        'role' => $user['role']
    ]);

    Response::json([
        'token' => $token,
        'user' => $user
    ], true, 'Giriş başarılı.');
} else {
    Response::error('Geçersiz e-posta veya şifre.', 401, 'INVALID_CREDENTIALS');
}
