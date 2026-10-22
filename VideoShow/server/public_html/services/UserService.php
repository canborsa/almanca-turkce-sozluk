<?php
// VideoShow/server/public_html/services/UserService.php

require_once __DIR__ . '/../includes/Database.php';

class UserService {
    private PDO $db;

    public function __construct() {
        $this->db = Database::getInstance();
    }

    public function register(string $username, string $email, string $password, ?string $displayName = null): array {
        $stmt = $this->db->prepare("SELECT id FROM users WHERE username = ? OR email = ?");
        $stmt->execute([$username, $email]);
        if ($stmt->fetch()) {
            throw new Exception("Kullanıcı adı veya e-posta adresi zaten kullanımda.");
        }

        $hash = password_hash($password, PASSWORD_BCRYPT);
        $displayName = $displayName ?: $username;

        $stmt = $this->db->prepare("
            INSERT INTO users (username, email, password_hash, display_name)
            VALUES (?, ?, ?, ?)
        ");
        $stmt->execute([$username, $email, $hash, $displayName]);
        $userId = (int)$this->db->lastInsertId();

        return $this->getUserById($userId);
    }

    public function login(string $usernameOrEmail, string $password): array {
        $stmt = $this->db->prepare("SELECT * FROM users WHERE username = ? OR email = ?");
        $stmt->execute([$usernameOrEmail, $usernameOrEmail]);
        $user = $stmt->fetch();

        if (!$user || !password_verify($password, $user['password_hash'])) {
            throw new Exception("Kullanıcı adı veya şifre hatalı.");
        }

        if (!$user['is_active']) {
            throw new Exception("Hesabınız pasifleştirilmiştir.");
        }

        // Update last login
        $stmt = $this->db->prepare("UPDATE users SET last_login_at = CURRENT_TIMESTAMP WHERE id = ?");
        $stmt->execute([$user['id']]);

        unset($user['password_hash']);
        return $user;
    }

    public function getUserById(int $id): array {
        $stmt = $this->db->prepare("SELECT id, username, email, display_name, avatar_url, bio, role, is_active, created_at FROM users WHERE id = ?");
        $stmt->execute([$id]);
        $user = $stmt->fetch();

        if (!$user) {
            throw new Exception("Kullanıcı bulunamadı.");
        }
        return $user;
    }

    public function getUserProfileStats(int $userId): array {
        $user = $this->getUserById($userId);

        $stmt = $this->db->prepare("SELECT COUNT(*) FROM watched WHERE user_id = ?");
        $stmt->execute([$userId]);
        $stats['watched_count'] = (int)$stmt->fetchColumn();

        $stmt = $this->db->prepare("SELECT COUNT(*) FROM watchlist WHERE user_id = ?");
        $stmt->execute([$userId]);
        $stats['watchlist_count'] = (int)$stmt->fetchColumn();

        $stmt = $this->db->prepare("SELECT COUNT(*) FROM favorites WHERE user_id = ?");
        $stmt->execute([$userId]);
        $stats['favorites_count'] = (int)$stmt->fetchColumn();

        $stmt = $this->db->prepare("SELECT COUNT(*), AVG(rating) FROM ratings WHERE user_id = ?");
        $stmt->execute([$userId]);
        $row = $stmt->fetch();
        $stats['ratings_count'] = (int)$row['COUNT(*)'];
        $stats['avg_rating'] = round((float)($row['AVG(rating)'] ?: 0), 2);

        $stmt = $this->db->prepare("SELECT COUNT(*) FROM reviews WHERE user_id = ?");
        $stmt->execute([$userId]);
        $stats['reviews_count'] = (int)$stmt->fetchColumn();

        $stats['user'] = $user;
        return $stats;
    }
}
