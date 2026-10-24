<?php
// Config: Security & CORS Rules
return [
    'allowed_origins' => ['*'],
    'rate_limit' => [
        'general' => ['requests' => 120, 'decay_seconds' => 60],
        'auth' => ['requests' => 10, 'decay_seconds' => 60],
        'review' => ['requests' => 20, 'decay_seconds' => 60]
    ]
];
