<?php
// VideoShow/server/public_html/includes/Validator.php

class Validator {
    public static function sanitizeString(?string $input): string {
        if ($input === null) return '';
        return htmlspecialchars(trim($input), ENT_QUOTES, 'UTF-8');
    }

    public static function isValidEmail(string $email): bool {
        return filter_var($email, FILTER_VALIDATE_EMAIL) !== false;
    }

    public static function sanitizeInt($input, int $default = 0): int {
        return filter_var($input, FILTER_VALIDATE_INT) !== false ? (int)$input : $default;
    }

    public static function sanitizeFloat($input, float $default = 0.0): float {
        return filter_var($input, FILTER_VALIDATE_FLOAT) !== false ? (float)$input : $default;
    }
}
