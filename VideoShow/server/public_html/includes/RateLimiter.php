<?php
// VideoShow/server/public_html/includes/RateLimiter.php

class RateLimiter {
    public static function check(string $action, string $identifier, int $maxRequests, int $windowSeconds): bool {
        $cacheDir = __DIR__ . '/../storage/cache/rate_limits';
        if (!is_dir($cacheDir)) {
            @mkdir($cacheDir, 0777, true);
        }

        $key = md5("{$action}_{$identifier}");
        $file = "{$cacheDir}/{$key}.json";
        $currentTime = time();

        $data = ['requests' => [], 'blocked_until' => 0];
        if (file_exists($file)) {
            $content = @file_get_contents($file);
            if ($content) {
                $data = json_decode($content, true) ?: $data;
            }
        }

        if ($data['blocked_until'] > $currentTime) {
            return false;
        }

        // Filter requests within window
        $data['requests'] = array_filter($data['requests'], function($timestamp) use ($currentTime, $windowSeconds) {
            return ($currentTime - $timestamp) < $windowSeconds;
        });

        if (count($data['requests']) >= $maxRequests) {
            $data['blocked_until'] = $currentTime + $windowSeconds;
            @file_put_contents($file, json_encode($data));
            return false;
        }

        $data['requests'][] = $currentTime;
        @file_put_contents($file, json_encode($data));
        return true;
    }
}
