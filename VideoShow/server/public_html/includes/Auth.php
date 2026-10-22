<?php
// VideoShow/server/public_html/includes/Auth.php

class Auth {
    private static ?array $jwtConfig = null;

    private static function getConfig(): array {
        if (self::$jwtConfig === null) {
            self::$jwtConfig = require __DIR__ . '/../config/auth.php';
        }
        return self::$jwtConfig;
    }

    public static function generateToken(array $payload, int $expiry = 86400): string {
        $config = self::getConfig();
        $header = json_encode(['typ' => 'JWT', 'alg' => 'HS256']);

        $payload['iat'] = time();
        $payload['exp'] = time() + $expiry;
        $payloadJson = json_encode($payload);

        $base64UrlHeader = self::base64UrlEncode($header);
        $base64UrlPayload = self::base64UrlEncode($payloadJson);

        $signature = hash_hmac('sha256', $base64UrlHeader . "." . $base64UrlPayload, $config['jwt_secret'], true);
        $base64UrlSignature = self::base64UrlEncode($signature);

        return $base64UrlHeader . "." . $base64UrlPayload . "." . $base64UrlSignature;
    }

    public static function validateToken(?string $jwt): ?array {
        if (!$jwt) return null;
        $config = self::getConfig();

        $tokenParts = explode('.', $jwt);
        if (count($tokenParts) !== 3) return null;

        $header = self::base64UrlDecode($tokenParts[0]);
        $payload = self::base64UrlDecode($tokenParts[1]);
        $signatureProvided = $tokenParts[2];

        $base64UrlHeader = self::base64UrlEncode($header);
        $base64UrlPayload = self::base64UrlEncode($payload);
        $signature = hash_hmac('sha256', $base64UrlHeader . "." . $base64UrlPayload, $config['jwt_secret'], true);
        $base64UrlSignature = self::base64UrlEncode($signature);

        if (!hash_equals($base64UrlSignature, $signatureProvided)) {
            return null;
        }

        $data = json_decode($payload, true);
        if (isset($data['exp']) && $data['exp'] < time()) {
            return null;
        }

        return $data;
    }

    public static function getBearerToken(): ?string {
        $headers = function_exists('getallheaders') ? getallheaders() : [];
        $authHeader = $headers['Authorization'] ?? $headers['authorization'] ?? $_SERVER['HTTP_AUTHORIZATION'] ?? null;

        if ($authHeader && preg_match('/Bearer\s+(\S+)/i', $authHeader, $matches)) {
            return $matches[1];
        }
        return null;
    }

    public static function requireAuth(): array {
        $token = self::getBearerToken();
        $user = self::validateToken($token);

        if (!$user) {
            Response::error('Yetkisiz erişim. Lütfen giriş yapın.', 401, 'UNAUTHORIZED');
        }
        return $user;
    }

    public static function requireAdmin(): array {
        $user = self::requireAuth();
        if (($user['role'] ?? '') !== 'ADMIN') {
            Response::error('Bu işlem için yönetici yetkisi gereklidir.', 403, 'FORBIDDEN');
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
