<?php
// Config: TMDB API Configuration
return [
    'access_token' => $_ENV['TMDB_ACCESS_TOKEN'] ?? '',
    'api_key' => $_ENV['TMDB_API_KEY'] ?? '',
    'base_url' => 'https://api.themoviedb.org/3/',
    'image_base_url' => 'https://image.tmdb.org/t/p/',
    'default_language' => 'tr-TR',
    'fallback_language' => 'en-US',
    'rate_limit_per_second' => 40,
    'retry_attempts' => 3,
    'retry_delay_ms' => 500,
];
