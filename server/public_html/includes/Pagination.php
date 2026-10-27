<?php
class Pagination {
    public static function getParams(array $request): array {
        $page = isset($request['page']) ? max(1, (int)$request['page']) : 1;
        $perPage = isset($request['per_page']) ? min(100, max(1, (int)$request['per_page'])) : 20;
        $offset = ($page - 1) * $perPage;

        return [
            'page' => $page,
            'per_page' => $perPage,
            'offset' => $offset
        ];
    }

    public static function format(int $total, int $page, int $perPage): array {
        return [
            'page' => $page,
            'per_page' => $perPage,
            'total' => $total,
            'total_pages' => ceil($total / $perPage)
        ];
    }
}
