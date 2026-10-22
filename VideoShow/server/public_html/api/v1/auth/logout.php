<?php
// VideoShow/server/public_html/api/v1/auth/logout.php

require_once __DIR__ . '/../../../includes/Response.php';
require_once __DIR__ . '/../../../includes/Auth.php';

Auth::requireAuth();
Response::json(null, 200, "Başarıyla çıkış yapıldı.");
