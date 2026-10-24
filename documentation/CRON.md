# Cron Job Configurations

cPanel Cron Setup:

1. `0 3 * * * php /path/to/server/cron/tmdb_changed_movies.php?secret=YOUR_CRON_SECRET`
2. `0 4 * * * php /path/to/server/cron/tmdb_daily_import.php?secret=YOUR_CRON_SECRET`
3. `0 5 * * * php /path/to/server/cron/tmdb_people_update.php?secret=YOUR_CRON_SECRET`
4. `0 6 * * * php /path/to/server/cron/update_statistics.php?secret=YOUR_CRON_SECRET`
5. `0 7 * * * php /path/to/server/cron/cleanup.php?secret=YOUR_CRON_SECRET`
6. `*/30 * * * * php /path/to/server/cron/health_check.php?secret=YOUR_CRON_SECRET`
