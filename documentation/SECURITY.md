# Security Policy

1. **TMDB Key Isolation:** TMDB API credentials are stored strictly in server-side configuration/environment variables. The Android APK contains NO TMDB keys.
2. **Database Access:** The Android app communicates solely via HTTPS REST API v1. No direct database access is allowed.
3. **Password Hashing:** Passwords are hashed using PHP `password_hash()` with BCRYP/DEFAULT.
4. **SQL Injection Prevention:** All SQL queries are executed using PDO prepared statements with bound parameters.
5. **Rate Limiting & Protection:** Authentication and search endpoints enforce rate limiting to prevent spam and brute-force attempts.
6. **Cron Security:** Background scripts require `CRON_SECRET` validation when executed via HTTP request.
