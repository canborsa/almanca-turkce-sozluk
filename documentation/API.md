# Movie Platform API Documentation (v1)

Base URL: `https://your-domain.com/api/v1`

## Response Format

All API responses follow a standardized JSON structure:

### Success Response
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

### Error Response
```json
{
  "success": false,
  "data": null,
  "message": "Error description message",
  "error_code": "ERROR_CODE"
}
```

---

## Authentication Endpoints (`/auth`)

### 1. Register User
- **Method:** `POST`
- **URL:** `/auth/register.php`
- **Auth Required:** No
- **Request Body:**
  ```json
  {
    "username": "john_doe",
    "email": "john@example.com",
    "password": "securepassword123",
    "display_name": "John Doe"
  }
  ```
- **Response:**
  ```json
  {
    "success": true,
    "data": {
      "user": {
        "id": 1,
        "username": "john_doe",
        "email": "john@example.com",
        "display_name": "John Doe",
        "role": "USER"
      },
      "token": "JWT_ACCESS_TOKEN",
      "refresh_token": "REFRESH_TOKEN"
    },
    "message": "User registered successfully"
  }
  ```

### 2. Login User
- **Method:** `POST`
- **URL:** `/auth/login.php`
- **Auth Required:** No
- **Request Body:**
  ```json
  {
    "email": "john@example.com",
    "password": "securepassword123"
  }
  ```

---

## Movies Endpoints (`/movies`)

### 1. List Movies
- **Method:** `GET`
- **URL:** `/movies/list.php?page=1&per_page=20`
- **Auth Required:** No

### 2. Movie Detail
- **Method:** `GET`
- **URL:** `/movies/detail.php?id=1` or `?tmdb_id=157336`
- **Auth Required:** No

### 3. Search Movies
- **Method:** `GET`
- **URL:** `/movies/search.php?q=Interstellar&page=1`
- **Auth Required:** No

### 4. Advanced Discovery
- **Method:** `POST`
- **URL:** `/discovery/discover.php`
- **Auth Required:** No
- **Request Body:**
  ```json
  {
    "genres": [878, 53],
    "year_start": 2000,
    "year_end": 2026,
    "min_rating": 7.5,
    "min_votes": 10000,
    "min_runtime": 90,
    "max_runtime": 180,
    "sort_by": "vote_average_desc",
    "page": 1,
    "per_page": 20
  }
  ```
