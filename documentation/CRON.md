# Cron Jobs Documentation

## Scheduled Jobs Overview

Run these via cPanel Cron Jobs CLI or secured HTTP call using `?secret=CRON_SECRET`.

| Schedule | File | Description |
|---|---|---|
| Daily 03:00 | `tmdb_changed_movies.php` | Fetch TMDB changed IDs and update existing local database movies |
| Daily 04:00 | `tmdb_daily_import.php` | Batch process queued TMDB daily exported IDs |
| Daily 05:00 | `tmdb_people_update.php` | Sync updated actors/directors details |
| Daily 06:00 | `update_statistics.php` | Re-calculate Bayesian weighted ratings and user statistics |
| Daily 07:00 | `cleanup.php` | Clear temporary storage and expired cache files |
| Every 30 mins | `health_check.php` | Verify server storage, MySQL connection, and sync state |
