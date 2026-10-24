<?php
// VideoShow/server/public_html/api/v1/index.php

require_once __DIR__ . '/../../includes/Response.php';

Response::json([
    'name' => 'VideoShow REST API',
    'version' => '1.0.0',
    'status' => 'running'
], 200);
