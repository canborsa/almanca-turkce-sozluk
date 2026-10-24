<?php

require_once __DIR__ . '/../includes/Response.php';

$config = require __DIR__ . '/../config/auth.php';
$secret = $_GET['secret'] ?? '';

if ($secret !== $config['cron_secret']) {
    Response::error('Erişim engellendi. Geçersiz cron secret key.', 403, 'FORBIDDEN');
}

Response::json(['status' => 'OK', 'timestamp' => date('Y-m-d H:i:s')], 200, 'Sistem çalışır durumda.');
