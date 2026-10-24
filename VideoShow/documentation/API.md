# VideoShow REST API Documentation

Base URL: `https://domain.com/api/v1/`

## Authentication Header
`Authorization: Bearer <access_token>`

---

## Auth Endpoints

### 1. Register User
- **Method:** `POST`
- **URL:** `/api/v1/auth/register.php`
- **Body:**
  ```json
  {
    "username": "jules",
    "email": "jules@example.com",
    "password": "secretpassword",
    "display_name": "Jules Engineer"
  }
  ```
- **Response (201):**
  ```json
  {
    "success": true,
    "data": {
      "user": { "id": 1, "username": "jules", "email": "jules@example.com" },
      "access_token": "JWT_TOKEN",
      "token_type": "Bearer",
      "expires_in": 86400
    },
    "message": "Kayıt başarıyla tamamlandı."
  }
  ```

### 2. Login
- **Method:** `POST`
- **URL:** `/api/v1/auth/login.php`
- **Body:**
  ```json
  {
    "username": "jules",
    "password": "secretpassword"
  }
  ```

---

## Movies Endpoints

### 3. Discover / List Movies
- **Method:** `GET`
- **URL:** `/api/v1/movies/list.php`
- **Parameters:**
  - `genres`: comma-separated genre IDs (e.g., `878,53`)
  - `year_min`: minimum release year (e.g., `2000`)
  - `year_max`: maximum release year (e.g., `2026`)
  - `min_rating`: minimum vote_average (e.g., `7.5`)
  - `min_votes`: minimum vote_count (e.g., `10000`)
  - `runtime_min`: minimum runtime in mins (e.g., `90`)
  - `runtime_max`: maximum runtime in mins (e.g., `180`)
  - `sort_by`: `popularity`, `vote_average`, `weighted_rating`, `release_date`, `title`
  - `page`: page number (default `1`)
  - `per_page`: items per page (default `20`)

### 4. Movie Detail
- **Method:** `GET`
- **URL:** `/api/v1/movies/detail.php?id=1`

### 5. Search
- **Method:** `GET`
- **URL:** `/api/v1/movies/search.php?q=Interstellar`

---

## User Actions Endpoints

### 6. Rating Add/Update
- **Method:** `POST`
- **URL:** `/api/v1/ratings/add.php`
- **Body:** `{"movie_id": 1, "rating": 4.5}`

### 7. Reviews Create
- **Method:** `POST`
- **URL:** `/api/v1/reviews/create.php`
- **Body:** `{"movie_id": 1, "title": "Harika", "body": "Yorum metni...", "spoiler": false}`

### 8. Favorites / Watchlist / Watched
- **URLs:**
  - `/api/v1/favorites/add.php`, `/api/v1/favorites/remove.php`, `/api/v1/favorites/list.php`
  - `/api/v1/watchlist/add.php`, `/api/v1/watchlist/remove.php`, `/api/v1/watchlist/list.php`
  - `/api/v1/watched/add.php`, `/api/v1/watched/remove.php`, `/api/v1/watched/list.php`
