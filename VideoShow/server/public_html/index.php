<?php
// VideoShow/server/public_html/index.php

require_once __DIR__ . '/includes/Response.php';

Response::json([
    'app' => 'VideoShow Movie Platform Server',
    'status' => 'online',
    'api_docs' => '/documentation/API.md'
], 200);
