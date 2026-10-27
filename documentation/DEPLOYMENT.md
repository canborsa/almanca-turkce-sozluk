# Deployment Guide

1. Upload contents of `server/public_html/` to cPanel `public_html/` or desired web root subfolder.
2. Create MySQL Database and user via cPanel MySQL Manager.
3. Import `database/schema.sql`, `database/indexes.sql`, and `database/seed.sql`.
4. Configure `.env` or `server/public_html/config/` files with Database credentials, JWT Secret, TMDB API Key, and Cron Secret.
5. Setup cPanel Cron Jobs pointing to `server/public_html/cron/` PHP CLI scripts.
6. Verify file permissions: `storage/` directory must be writable (`0755`).
