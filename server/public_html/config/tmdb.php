<?php
return [
    'api_key' => $_ENV['TMDB_API_KEY'] ?? '',
    'bearer_token' => $_ENV['TMDB_BEARER_TOKEN'] ?? '',
    'base_url' => 'https://api.themoviedb.org/3/',
    'image_base_url' => 'https://image.tmdb.org/t/p/',
    'language' => 'tr-TR',
    'batch_size' => 50,
];
