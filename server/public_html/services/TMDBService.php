<?php
require_once __DIR__ . '/../includes/Logger.php';

class TMDBService {
    private string $baseUrl;
    private string $apiKey;
    private string $bearerToken;
    private string $language;

    public function __construct() {
        $config = require __DIR__ . '/../config/tmdb.php';
        $this->baseUrl = $config['base_url'];
        $this->apiKey = $config['api_key'];
        $this->bearerToken = $config['bearer_token'];
        $this->language = $config['language'];
    }

    public function fetch(string $endpoint, array $params = []): ?array {
        $params['language'] = $this->language;
        if ($this->apiKey) {
            $params['api_key'] = $this->apiKey;
        }

        $url = $this->baseUrl . ltrim($endpoint, '/') . '?' . http_build_query($params);

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_TIMEOUT, 15);

        if ($this->bearerToken) {
            curl_setopt($ch, CURLOPT_HTTPHEADER, [
                "Authorization: Bearer " . $this->bearerToken,
                "Accept: application/json"
            ]);
        }

        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $error = curl_error($ch);
        curl_close($ch);

        if ($error || $httpCode !== 200) {
            Logger::log('TMDB_ERROR', "Endpoint: $endpoint, HTTP Code: $httpCode, Error: $error");
            return null;
        }

        return json_decode($response, true);
    }

    public function getMovieDetails(int $tmdbId): ?array {
        return $this->fetch("movie/{$tmdbId}", ['append_to_response' => 'credits,keywords,translations']);
    }

    public function getChangedMovies(string $startDate): ?array {
        return $this->fetch('movie/changes', ['start_date' => $startDate]);
    }
}
