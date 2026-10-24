<?php
// VideoShow/server/public_html/admin/settings.php

require_once __DIR__ . '/../includes/Response.php';
require_once __DIR__ . '/../includes/Auth.php';

$admin = Auth::requireAdmin();
$appConfig = require __DIR__ . '/../config/app.php';

Response::json($appConfig, 200);
