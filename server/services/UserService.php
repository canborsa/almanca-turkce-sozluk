<?php

require_once __DIR__ . '/../includes/Database.php';
require_once __DIR__ . '/../includes/Auth.php';

class UserService {
    private PDO $db;

    public function __construct() {
        $this->db = Database::getInstance();
    }

    public function register(string $username, string $email, string $password, string $displayName): array {
        $stmt = $this->db->prepare("SELECT id FROM users WHERE username = :u OR email = :e LIMIT 1");
        $stmt->execute(['u' => $username, 'e' => $email]);
        if ($stmt->fetch()) {
            throw new Exception("Kullanıcı adı veya e-posta adresi zaten kullanımda.");
        }

        $hash = Auth::hashPassword($password);
        $insert = $this->db->prepare("
            INSERT INTO users (username, email, password_hash, display_name, role, is_active)
            VALUES (:u, :e, :p, :d, 'USER', 1)
        ");
        $insert->execute([
            'u' => $username,
            'e' => $email,
            'p' => $hash,
            'd' => $displayName
        ]);

        $userId = (int)$this->db->lastInsertId();
        $user = $this->getProfile($userId);

        $token = Auth::generateToken([
            'user_id' => $user['id'],
            'username' => $user['username'],
            'role' => $user['role']
        ]);

        return [
            'user' => $user,
            'token' => $token
        ];
    }

    public function login(string $email, string $password): array {
        $stmt = $this->db->prepare("SELECT * FROM users WHERE email = :e LIMIT 1");
        $stmt->execute(['e' => $email]);
        $user = $stmt->fetch();

        if (!$user || !Auth::verifyPassword($password, $user['password_hash'])) {
            throw new Exception("Geçersiz e-posta veya şifre.");
        }

        if (!$user['is_active']) {
            throw new Exception("Hesabınız askıya alınmıştır.");
        }

        // Update last login
        $this->db->prepare("UPDATE users SET last_login_at = NOW() WHERE id = :id")->execute(['id' => $user['id']]);

        unset($user['password_hash']);

        $token = Auth::generateToken([
            'user_id' => $user['id'],
            'username' => $user['username'],
            'role' => $user['role']
        ]);

        return [
            'user' => $user,
            'token' => $token
        ];
    }

    public function getProfile(int $userId): array {
        $stmt = $this->db->prepare("
            SELECT id, username, email, display_name, avatar_url, bio, role, is_active, created_at, last_login_at
            FROM users WHERE id = :id LIMIT 1
        ");
        $stmt->execute(['id' => $userId]);
        $user = $stmt->fetch();

        if (!$user) {
            throw new Exception("Kullanıcı bulunamadı.");
        }

        return $user;
    }
}
