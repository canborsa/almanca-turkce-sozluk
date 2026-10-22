<?php
// VideoShow/server/public_html/includes/Logger.php

class Logger {
    public static function log(string $level, string $message, array $context = []): void {
        $logDir = __DIR__ . '/../storage/logs';
        if (!is_dir($logDir)) {
            @mkdir($logDir, 0777, true);
        }

        // Sanitize context: Remove passwords, tokens, secrets
        unset($context['password'], $context['token'], $context['access_token'], $context['refresh_token']);

        $date = date('Y-m-d H:i:s');
        $logFile = $logDir . '/' . date('Y-m-d') . '.log';
        $formattedContext = !empty($context) ? json_encode($context, JSON_UNESCAPED_UNICODE) : '';
        $logEntry = "[$date] [$level] $message $formattedContext" . PHP_EOL;

        @file_put_contents($logFile, $logEntry, FILE_APPEND);
    }

    public static function info(string $message, array $context = []): void {
        self::log('INFO', $message, $context);
    }

    public static function error(string $message, array $context = []): void {
        self::log('ERROR', $message, $context);
    }
}
