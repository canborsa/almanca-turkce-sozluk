# Security Guidelines

1. TMDB credentials must remain strictly server-side.
2. All passwords hashed using PHP `password_hash()` with `PASSWORD_ARGON2ID` or `PASSWORD_BCRYPT`.
3. All database interactions use PDO Prepared Statements.
4. JWT tokens use strong secret keys stored in config/environment variables.
5. Rate limiting applied to register, login, review, and sync endpoints.
