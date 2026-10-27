<?php
class RateLimiter {
    public static function check(string $key, int $limit = 60, int $window = 60): bool {
        $cacheFile = __DIR__ . '/../storage/cache/rate_' . md5($key) . '.json';
        $now = time();
        $data = ['count' => 0, 'start' => $now];

        if (file_exists($cacheFile)) {
            $content = json_decode(file_get_contents($cacheFile), true);
            if ($content && ($now - $content['start']) < $window) {
                $data = $content;
            }
        }

        $data['count']++;
        file_put_contents($cacheFile, json_encode($data));

        return $data['count'] <= $limit;
    }
}
