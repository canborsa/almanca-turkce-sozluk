<?php
// VideoShow/server/public_html/config/tmdb.php

return [
    'access_token' => getenv('TMDB_ACCESS_TOKEN') ?: 'MOCK_TMDB_ACCESS_TOKEN',
    'api_key'      => getenv('TMDB_API_KEY') ?: 'MOCK_TMDB_API_KEY',
    'base_url'     => 'https://api.themoviedb.org/3',
    'image_base'   => 'https://image.tmdb.org/t/p/',
    'poster_sizes' => ['w185', 'w342', 'w500', 'original'],
    'backdrop_sizes' => ['w300', 'w780', 'w1280', 'original'],
    'profile_sizes' => ['w45', 'w185', 'h632', 'original'],
    'language'     => 'tr-TR',
    'fallback_language' => 'en-US'
];
