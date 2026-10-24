<?php

class TMDBService {
    private array $config;

    public function __construct() {
        $this->config = require __DIR__ . '/../config/tmdb.php';
    }

    public function fetchMovieDetail(int $tmdbId, string $language = 'tr-TR'): ?array {
        $endpoint = "movie/{$tmdbId}?append_to_response=credits,keywords,videos,translations,images&language={$language}";
        return $this->request($endpoint);
    }

    public function fetchPersonDetail(int $personId, string $language = 'tr-TR'): ?array {
        $endpoint = "person/{$personId}?append_to_response=movie_credits,images&language={$language}";
        return $this->request($endpoint);
    }

    public function searchMovies(string $query, int $page = 1, string $language = 'tr-TR'): ?array {
        $encodedQuery = urlencode($query);
        $endpoint = "search/movie?query={$encodedQuery}&page={$page}&language={$language}";
        return $this->request($endpoint);
    }

    public function fetchChangedMovieIds(string $startDate): ?array {
        $endpoint = "movie/changes?start_date={$startDate}";
        return $this->request($endpoint);
    }

    private function request(string $endpoint): ?array {
        $url = $this->config['base_url'] . $endpoint;
        $headers = [
            'Accept: application/json',
            'Authorization: Bearer ' . $this->config['access_token']
        ];

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_TIMEOUT, 10);

        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        curl_close($ch);

        if ($httpCode >= 200 && $httpCode < 300 && $response) {
            return json_decode($response, true);
        }

        return null;
    }
}
