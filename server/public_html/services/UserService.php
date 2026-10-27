<?php
require_once __DIR__ . '/../includes/Database.php';

class UserService {
    private PDO $db;

    public function __construct() {
        $this->db = Database::getInstance();
    }

    public function register(string $username, string $email, string $password, ?string $displayName = null): bool {
        $hash = password_hash($password, PASSWORD_DEFAULT);
        $stmt = $this->db->prepare("INSERT INTO users (username, email, password_hash, display_name) VALUES (:username, :email, :hash, :display_name)");
        return $stmt->execute([
            ':username' => $username,
            ':email' => $email,
            ':hash' => $hash,
            ':display_name' => $displayName ?? $username
        ]);
    }

    public function login(string $email, string $password): ?array {
        $stmt = $this->db->prepare("SELECT * FROM users WHERE email = :email AND is_active = 1 LIMIT 1");
        $stmt->execute([':email' => $email]);
        $user = $stmt->fetch();

        if ($user && password_verify($password, $user['password_hash'])) {
            $update = $this->db->prepare("UPDATE users SET last_login_at = CURRENT_TIMESTAMP WHERE id = :id");
            $update->execute([':id' => $user['id']]);
            unset($user['password_hash']);
            return $user;
        }

        return null;
    }
}
