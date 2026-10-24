<?php
// Config: App Settings
return [
    'name' => 'VideoShow Movie Platform',
    'env' => $_ENV['APP_ENV'] ?? 'development',
    'debug' => true,
    'timezone' => 'Europe/Istanbul',
    'url' => $_ENV['APP_URL'] ?? 'http://localhost',
    'default_per_page' => 20,
    'max_per_page' => 100,
];
