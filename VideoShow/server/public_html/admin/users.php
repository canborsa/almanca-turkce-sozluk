<?php
// VideoShow/server/public_html/admin/users.php

require_once __DIR__ . '/../includes/Response.php';
require_once __DIR__ . '/../includes/Auth.php';
require_once __DIR__ . '/../includes/Database.php';

$admin = Auth::requireAdmin();
$db = Database::getInstance();

$stmt = $db->query("SELECT id, username, email, display_name, role, is_active, created_at, last_login_at FROM users ORDER BY id DESC");
$users = $stmt->fetchAll();

Response::json($users, 200);
