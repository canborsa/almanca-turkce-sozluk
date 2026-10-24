<?php
// VideoShow/server/public_html/includes/Database.php

class Database {
    private static ?PDO $instance = null;

    public static function getInstance(): PDO {
        if (self::$instance === null) {
            $config = require __DIR__ . '/../config/database.php';

            if ($config['driver'] === 'sqlite') {
                $dsn = 'sqlite:' . $config['sqlite_path'];
                self::$instance = new PDO($dsn, null, null, [
                    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
                ]);
            } else {
                try {
                    $dsn = "mysql:host={$config['host']};port={$config['port']};dbname={$config['dbname']};charset={$config['charset']}";
                    self::$instance = new PDO($dsn, $config['username'], $config['password'], $config['options']);
                } catch (PDOException $e) {
                    // Fallback to SQLite if MySQL is unavailable in dev/test environment
                    if (file_exists($config['sqlite_path'])) {
                        $dsn = 'sqlite:' . $config['sqlite_path'];
                        self::$instance = new PDO($dsn, null, null, [
                            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
                        ]);
                    } else {
                        throw $e;
                    }
                }
            }
        }
        return self::$instance;
    }

    public static function setInstance(PDO $pdo): void {
        self::$instance = $pdo;
    }
}
