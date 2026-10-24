<?php

class Response {
    public static function json($data = null, int $statusCode = 200, ?string $message = null, ?array $pagination = null): void {
        http_response_code($statusCode);
        header('Content-Type: application/json; charset=utf-8');
        header('Access-Control-Allow-Origin: *');
        header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
        header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');

        if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
            exit(0);
        }

        $response = [
            'success' => $statusCode >= 200 && $statusCode < 300,
            'data' => $data,
            'message' => $message,
        ];

        if ($pagination !== null) {
            $response['pagination'] = $pagination;
        }

        echo json_encode($response, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
        exit;
    }

    public static function error(string $message, int $statusCode = 400, string $errorCode = 'BAD_REQUEST', $data = null): void {
        http_response_code($statusCode);
        header('Content-Type: application/json; charset=utf-8');
        header('Access-Control-Allow-Origin: *');

        echo json_encode([
            'success' => false,
            'data' => $data,
            'message' => $message,
            'error_code' => $errorCode
        ], JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
        exit;
    }
}
