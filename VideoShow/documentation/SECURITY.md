# VideoShow Security Policies

1. **No Client Secrets:** TMDB API Key / Bearer tokens are NEVER stored in the Android APK.
2. **Prepared Statements:** All SQL queries use PDO prepared statements to guarantee 100% protection against SQL Injection.
3. **Password Security:** BCRYPT password hashing (`password_hash()`) is strictly used.
4. **Rate Limiting:** IP and User based rate limiters protect auth, review, and search endpoints against brute-force/spam attacks.
5. **Sanitization:** Input sanitization via `htmlspecialchars()` and strict filter validators prevent XSS.
