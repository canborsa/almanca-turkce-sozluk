<?php
require_once __DIR__ . '/Response.php';

class Auth {
    public static function generateToken(array $payload): string {
        $config = require __DIR__ . '/../config/auth.php';
        $header = json_encode(['typ' => 'JWT', 'alg' => 'HS256']);

        $payload['exp'] = time() + $config['access_token_expiry'];
        $payloadStr = json_encode($payload);

        $base64UrlHeader = self::base64UrlEncode($header);
        $base64UrlPayload = self::base64UrlEncode($payloadStr);

        $signature = hash_hmac('sha256', $base64UrlHeader . "." . $base64UrlPayload, $config['jwt_secret'], true);
        $base64UrlSignature = self::base64UrlEncode($signature);

        return $base64UrlHeader . "." . $base64UrlPayload . "." . $base64UrlSignature;
    }

    public static function validateToken(): ?array {
        $headers = getallheaders();
        $authHeader = $headers['Authorization'] ?? $headers['authorization'] ?? '';

        if (!preg_match('/Bearer\s(\S+)/', $authHeader, $matches)) {
            return null;
        }

        $jwt = $matches[1];
        $tokenParts = explode('.', $jwt);
        if (count($tokenParts) !== 3) {
            return null;
        }

        $config = require __DIR__ . '/../config/auth.php';
        $signature = self::base64UrlEncode(hash_hmac('sha256', $tokenParts[0] . "." . $tokenParts[1], $config['jwt_secret'], true));

        if ($signature !== $tokenParts[2]) {
            return null;
        }

        $payload = json_decode(self::base64UrlDecode($tokenParts[1]), true);
        if (isset($payload['exp']) && $payload['exp'] < time()) {
            return null;
        }

        return $payload;
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
        return base64_decode(str_pad(strtr($data, '-_', '+/'), strlen($data) % 4, '=', STR_PAD_RIGHT));
    }
}
