<?php
require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';

$user = Auth::requireAuth();
Response::json(['user' => $user], true, 'Oturum bilgileri alındı.');
