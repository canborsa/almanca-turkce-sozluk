<?php
class Response {
    public static function json($data = null, bool $success = true, ?string $message = null, int $statusCode = 200, ?array $pagination = null): void {
        http_response_code($statusCode);
        header('Content-Type: application/json; charset=utf-8');

        $response = [
            'success' => $success,
            'data' => $data,
            'message' => $message,
        ];

        if ($pagination !== null) {
            $response['pagination'] = $pagination;
        }

        echo json_encode($response, JSON_UNESCAPED_UNICODE);
        exit;
    }

    public static function error(string $message, int $statusCode = 400, ?string $errorCode = null): void {
        http_response_code($statusCode);
        header('Content-Type: application/json; charset=utf-8');

        echo json_encode([
            'success' => false,
            'data' => null,
            'message' => $message,
            'error_code' => $errorCode ?? 'ERROR'
        ], JSON_UNESCAPED_UNICODE);
        exit;
    }
}
