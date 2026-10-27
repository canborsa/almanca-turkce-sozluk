<?php
class Logger {
    public static function log(string $type, string $message): void {
        $logFile = __DIR__ . '/../storage/logs/' . date('Y-m-d') . '.log';
        $entry = "[" . date('Y-m-d H:i:s') . "] [$type] $message" . PHP_EOL;
        file_put_contents($logFile, $entry, FILE_APPEND);
    }
}
