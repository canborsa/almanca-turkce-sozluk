<?php

class Validator {
    public static function sanitizeString(?string $str): string {
        if ($str === null) return '';
        return htmlspecialchars(trim($str), ENT_QUOTES, 'UTF-8');
    }

    public static function isValidEmail(string $email): bool {
        return filter_var($email, FILTER_VALIDATE_EMAIL) !== false;
    }

    public static function getJsonInput(): array {
        $raw = file_get_contents('php://input');
        if (empty($raw)) return [];
        $data = json_decode($raw, true);
        return is_array($data) ? $data : [];
    }
}
