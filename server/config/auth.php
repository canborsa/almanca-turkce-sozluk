<?php
// Config: Authentication and JWT Secrets
return [
    'jwt_secret' => $_ENV['JWT_SECRET'] ?? 'super_secret_jwt_key_change_in_prod_12345!',
    'jwt_algo' => 'HS256',
    'access_token_ttl' => 86400, // 24 hours in seconds
    'refresh_token_ttl' => 2592000, // 30 days in seconds
    'password_algo' => PASSWORD_BCRYPT,
    'cron_secret' => $_ENV['CRON_SECRET'] ?? 'movie_cron_secret_key_87654321',
];
