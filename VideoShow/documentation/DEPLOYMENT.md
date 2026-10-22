# VideoShow cPanel Server Deployment Guide

## Prerequisites
- cPanel Hosting with PHP 8.1+ & MySQL 8.0+ / MariaDB
- HTTPS / SSL Certificate enabled
- PDO Extension enabled

## Steps

1. Upload contents of `VideoShow/server/public_html/` to cPanel `public_html/` or a subfolder `api/`.
2. Create MySQL Database in cPanel and import:
   - `VideoShow/database/schema.sql`
   - `VideoShow/database/indexes.sql`
   - `VideoShow/database/seed.sql`
3. Configure Environment Variables / Configs in `config/database.php`, `config/tmdb.php`, `config/auth.php`.
4. Configure cPanel Cron Jobs as documented in `CRON.md`.
