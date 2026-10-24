<?php
// VideoShow/server/public_html/includes/Pagination.php

class Pagination {
    public static function getParams(array $request): array {
        $page = max(1, Validator::sanitizeInt($request['page'] ?? 1, 1));
        $perPage = min(100, max(1, Validator::sanitizeInt($request['per_page'] ?? 20, 20)));
        $offset = ($page - 1) * $perPage;

        return [
            'page'     => $page,
            'per_page' => $perPage,
            'offset'   => $offset
        ];
    }

    public static function format(int $total, int $page, int $perPage): array {
        return [
            'page'        => $page,
            'per_page'    => $perPage,
            'total'       => $total,
            'total_pages' => max(1, ceil($total / $perPage))
        ];
    }
}
