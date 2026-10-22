<?php
// VideoShow/server/public_html/config/auth.php

return [
    'jwt_secret'          => getenv('JWT_SECRET') ?: 'VideoShow_Super_Secret_Key_2026_ChangeInProduction!',
    'access_token_expiry' => 86400, // 24 hours
    'refresh_token_expiry'=> 2592000, // 30 days
    'cron_secret'         => getenv('CRON_SECRET') ?: 'CRON_SECRET_TOKEN_2026'
];
