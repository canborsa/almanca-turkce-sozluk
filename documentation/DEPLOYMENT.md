# Deployment Guide

## Server Deployment
1. Upload contents of `server/public_html` to cPanel `public_html` directory or subdomain folder.
2. Keep `server/config` and `server/services` outside public docroot if possible or ensure `.htaccess` protects sensitive config files.
3. Import `database/schema.sql`, `database/indexes.sql`, and `database/seed.sql` into MySQL database.
4. Set environment configuration in `server/config/database.php` and `server/config/tmdb.php`.

## Android Application Build
1. Set `API_BASE_URL` in `android/app/build.gradle.kts`.
2. Build APK/AAB with `./gradlew assembleRelease` or `./gradlew bundleRelease`.
