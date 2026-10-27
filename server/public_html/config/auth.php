<?php
return [
    'jwt_secret' => $_ENV['JWT_SECRET'] ?? 'supersecretkeychangeinproduction123456789!',
    'access_token_expiry' => 86400, // 24 hours
    'refresh_token_expiry' => 2592000, // 30 days
];
