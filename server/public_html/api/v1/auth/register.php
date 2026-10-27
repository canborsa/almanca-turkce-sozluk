<?php
require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../services/UserService.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    Response::error('Sadece POST metodu desteklenmektedir.', 405);
}

$input = json_decode(file_get_contents('php://input'), true);

$username = trim($input['username'] ?? '');
$email = trim($input['email'] ?? '');
$password = trim($input['password'] ?? '');
$displayName = trim($input['display_name'] ?? '');

if (empty($username) || empty($email) || empty($password)) {
    Response::error('Lütfen tüm zorunlu alanları doldurun.');
}

$userService = new UserService();
if ($userService->register($username, $email, $password, $displayName)) {
    Response::json(null, true, 'Kayıt başarılı. Giriş yapabilirsiniz.', 201);
} else {
    Response::error('Kayıt oluşturulamadı. Kullanıcı adı veya e-posta kullanımda olabilir.');
}
