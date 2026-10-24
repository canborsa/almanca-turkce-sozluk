<?php
// VideoShow/server/public_html/cron/health_check.php

require_once __DIR__ . '/../includes/Response.php';
require_once __DIR__ . '/../includes/Database.php';

try {
    $db = Database::getInstance();
    $db->query("SELECT 1");
    Response::json(['status' => 'healthy', 'timestamp' => date('Y-m-d H:i:s')], 200);
} catch (Exception $e) {
    Response::error("System unhealthy: " . $e->getMessage(), 500);
}
