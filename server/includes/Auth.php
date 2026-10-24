<?php

class Auth {
    private static array $config;

    private static function initConfig(): void {
        if (!isset(self::$config)) {
            self::$config = require __DIR__ . '/../config/auth.php';
        }
    }

    public static function hashPassword(string $password): string {
        self::initConfig();
        return password_hash($password, self::$config['password_algo']);
    }

    public static function verifyPassword(string $password, string $hash): bool {
        return password_verify($password, $hash);
    }

    public static function generateToken(array $payload): string {
        self::initConfig();
        $header = json_encode(['typ' => 'JWT', 'alg' => self::$config['jwt_algo']]);
        $payload['iat'] = time();
        $payload['exp'] = time() + self::$config['access_token_ttl'];
        $payloadJson = json_encode($payload);

        $base64UrlHeader = self::base64UrlEncode($header);
        $base64UrlPayload = self::base64UrlEncode($payloadJson);

        $signature = hash_hmac('sha256', $base64UrlHeader . "." . $base64UrlPayload, self::$config['jwt_secret'], true);
        $base64UrlSignature = self::base64UrlEncode($signature);

        return $base64UrlHeader . "." . $base64UrlPayload . "." . $base64UrlSignature;
    }

    public static function validateToken(): ?array {
        self::initConfig();
        $authHeader = $_SERVER['HTTP_AUTHORIZATION'] ?? $_SERVER['REDIRECT_HTTP_AUTHORIZATION'] ?? '';
        if (!preg_match('/Bearer\s(\S+)/', $authHeader, $matches)) {
            return null;
        }

        $jwt = $matches[1];
        $tokenParts = explode('.', $jwt);
        if (count($tokenParts) !== 3) {
            return null;
        }

        list($header, $payload, $signature) = $tokenParts;

        $validSignature = self::base64UrlEncode(hash_hmac('sha256', $header . "." . $payload, self::$config['jwt_secret'], true));

        if (!hash_equals($validSignature, $signature)) {
            return null;
        }

        $data = json_decode(self::base64UrlDecode($payload), true);
        if (!$data || !isset($data['exp']) || $data['exp'] < time()) {
            return null;
        }

        return $data;
    }

    public static function requireAuth(): array {
        $user = self::validateToken();
        if (!$user) {
            Response::error('Yetkisiz erişim. Lütfen giriş yapın.', 401, 'AUTH_REQUIRED');
        }
        return $user;
    }

    public static function requireAdmin(): array {
        $user = self::requireAuth();
        if (($user['role'] ?? '') !== 'ADMIN') {
            Response::error('Bu işlem için admin yetkisi gereklidir.', 403, 'FORBIDDEN');
        }
        return $user;
    }

    private static function base64UrlEncode(string $data): string {
        return rtrim(strtr(base64_encode($data), '+/', '-_'), '=');
    }

    private static function base64UrlDecode(string $data): string {
        return base64_decode(strtr($data, '-_', '+/'));
    }
}
