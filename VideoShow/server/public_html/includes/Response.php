<?php
// VideoShow/server/public_html/includes/Response.php

class Response {
    public static function json($data = null, int $statusCode = 200, ?string $message = null, ?array $pagination = null): void {
        http_response_code($statusCode);
        header('Content-Type: application/json; charset=utf-8');

        $response = [
            'success' => $statusCode >= 200 && $statusCode < 300,
            'data'    => $data,
            'message' => $message
        ];

        if ($pagination !== null) {
            $response['pagination'] = $pagination;
        }

        echo json_encode($response, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
        exit;
    }

    public static function error(string $message, int $statusCode = 400, ?string $errorCode = 'ERROR'): void {
        http_response_code($statusCode);
        header('Content-Type: application/json; charset=utf-8');

        $response = [
            'success'    => false,
            'data'       => null,
            'message'    => $message,
            'error_code' => $errorCode
        ];

        echo json_encode($response, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
        exit;
    }
}
