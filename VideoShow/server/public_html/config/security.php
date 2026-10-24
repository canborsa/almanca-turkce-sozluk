<?php
// VideoShow/server/public_html/config/security.php

return [
    'rate_limits' => [
        'login'    => ['requests' => 10, 'window' => 300], // 10 attempts per 5 min
        'register' => ['requests' => 5, 'window' => 600],  // 5 attempts per 10 min
        'api'      => ['requests' => 100, 'window' => 60], // 100 requests per 1 min
        'review'   => ['requests' => 15, 'window' => 60]   // 15 reviews per 1 min
    ]
];
