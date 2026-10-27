# API Documentation (v1)

## Base URL
`https://domain.com/api/v1/`

## Response Format
```json
{
  "success": true,
  "data": {},
  "message": null,
  "pagination": {
    "page": 1,
    "per_page": 20,
    "total": 100,
    "total_pages": 5
  }
}
```

## Endpoints Summary

### Auth
- `POST /api/v1/auth/register.php` - Register new user
- `POST /api/v1/auth/login.php` - User login & JWT issuance
- `POST /api/v1/auth/refresh.php` - Refresh token
- `GET /api/v1/auth/me.php` - Authenticated user info

### Movies
- `GET /api/v1/movies/list.php` - Paged movie list
- `GET /api/v1/movies/detail.php?id={id}` - Movie details, genres, credits
- `GET /api/v1/movies/trending.php` - Trending movies
- `GET /api/v1/movies/popular.php` - Popular movies
- `GET /api/v1/movies/top_rated.php` - Top rated movies
- `GET /api/v1/movies/recommendations.php` - Personalized recommendations

### Discovery & Search
- `GET /api/v1/discovery/discover.php` - Multi-criteria filter query
  - Params: `genres`, `year_min`, `year_max`, `rating_min`, `rating_max`, `votes_min`, `duration_min`, `duration_max`, `sort_by`
- `GET /api/v1/movies/search.php?q={query}` - Search movies & people

### Social & Interactions
- `POST /api/v1/ratings/add.php` - Add/Update user rating
- `POST /api/v1/reviews/create.php` - Submit review (with spoiler option)
- `POST /api/v1/favorites/add.php` - Add to favorites
- `POST /api/v1/watchlist/add.php` - Add to watchlist
- `POST /api/v1/watched/add.php` - Log watch history
