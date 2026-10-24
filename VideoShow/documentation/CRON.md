# VideoShow Cron Jobs Documentation

## Scheduled Tasks Configuration (cPanel Cron)

1. **Daily Changed Movies Sync (Every night at 03:00)**
   `0 3 * * * php /home/username/public_html/cron/tmdb_changed_movies.php`

2. **Daily Imports Check (Every night at 04:00)**
   `0 4 * * * php /home/username/public_html/cron/tmdb_daily_import.php`

3. **People Update (Every night at 05:00)**
   `0 5 * * * php /home/username/public_html/cron/tmdb_people_update.php`

4. **Statistics & Bayesian Ratings Recalculation (Every night at 06:00)**
   `0 6 * * * php /home/username/public_html/cron/update_statistics.php`

5. **Cache & Logs Cleanup (Every night at 07:00)**
   `0 7 * * * php /home/username/public_html/cron/cleanup.php`

6. **Health Check (Every 30 minutes)**
   `*/30 * * * * php /home/username/public_html/cron/health_check.php`

---

## Security
If invoked over HTTP instead of CLI, append `?secret=CRON_SECRET_TOKEN` to prevent unauthorized execution.
