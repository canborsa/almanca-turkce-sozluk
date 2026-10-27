<?php
return [
    'name' => 'Movie Platform API',
    'version' => '1.0.0',
    'cron_secret' => $_ENV['CRON_SECRET'] ?? 'cronsecretkey123',
    'cache_ttl' => 3600,
];
