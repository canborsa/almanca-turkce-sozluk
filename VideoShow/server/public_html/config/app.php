<?php
// VideoShow/server/public_html/config/app.php

return [
    'name'        => 'VideoShow Movie Platform',
    'env'         => getenv('APP_ENV') ?: 'production',
    'url'         => getenv('APP_URL') ?: 'https://localhost',
    'timezone'    => 'Europe/Istanbul',
    'cache_ttl'   => 3600 // 1 hour
];
