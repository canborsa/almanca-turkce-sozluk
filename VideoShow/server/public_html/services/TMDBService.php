<?php
// VideoShow/server/public_html/services/TMDBService.php

require_once __DIR__ . '/../includes/Logger.php';

class TMDBService {
    private array $config;

    public function __construct() {
        $this->config = require __DIR__ . '/../config/tmdb.php';
    }

    public function getMovieDetails(int $tmdbId, string $language = 'tr-TR'): ?array {
        $endpoint = "/movie/{$tmdbId}?append_to_response=credits,keywords,translations,images&language={$language}";
        return $this->request($endpoint);
    }

    public function getPersonDetails(int $personTmdbId, string $language = 'tr-TR'): ?array {
        $endpoint = "/person/{$personTmdbId}?append_to_response=movie_credits&language={$language}";
        return $this->request($endpoint);
    }

    public function getMovieChanges(string $startDate, string $endDate, int $page = 1): ?array {
        $endpoint = "/movie/changes?start_date={$startDate}&end_date={$endDate}&page={$page}";
        return $this->request($endpoint);
    }

    public function searchMovies(string $query, int $page = 1, string $language = 'tr-TR'): ?array {
        $encodedQuery = urlencode($query);
        $endpoint = "/search/movie?query={$encodedQuery}&page={$page}&language={$language}";
        return $this->request($endpoint);
    }

    public function getGenres(string $language = 'tr-TR'): ?array {
        $endpoint = "/genre/movie/list?language={$language}";
        return $this->request($endpoint);
    }

    private function request(string $endpoint): ?array {
        $url = $this->config['base_url'] . $endpoint;

        // Mock fallback if token is default mock token or testing offline
        if ($this->config['access_token'] === 'MOCK_TMDB_ACCESS_TOKEN') {
            return $this->getMockResponse($endpoint);
        }

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_TIMEOUT, 10);
        curl_setopt($ch, CURLOPT_HTTPHEADER, [
            "Authorization: Bearer " . $this->config['access_token'],
            "Accept: application/json"
        ]);

        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $error = curl_error($ch);
        curl_close($ch);

        if ($error || $httpCode !== 200) {
            Logger::error("TMDB API Error ($httpCode): $error", ['endpoint' => $endpoint]);
            return $this->getMockResponse($endpoint);
        }

        return json_decode($response, true);
    }

    private function getMockResponse(string $endpoint): array {
        if (strpos($endpoint, '/movie/') === 0 && !strpos($endpoint, '/changes') && !strpos($endpoint, '/list')) {
            preg_match('/\/movie\/(\d+)/', $endpoint, $matches);
            $tmdbId = isset($matches[1]) ? (int)$matches[1] : 157336;

            return [
                'id' => $tmdbId,
                'imdb_id' => 'tt0816692',
                'title' => $tmdbId === 157336 ? 'Yıldızlararası' : 'Test Movie ' . $tmdbId,
                'original_title' => 'Interstellar',
                'overview' => 'İnsanlığın son günlerinde bir grup uzay kaşifi solucan deliğini kullanarak yeni bir yaşanabilir gezegen arar.',
                'tagline' => 'İnsanlığın sonu bizim sonumuz olmayacak.',
                'release_date' => '2014-11-05',
                'runtime' => 169,
                'poster_path' => '/gEU2QniE6E77NI6lCU6Mxl4vO2i.jpg',
                'backdrop_path' => '/xJHokMbljv3AA1S3i3z239339.jpg',
                'original_language' => 'en',
                'status' => 'Released',
                'adult' => false,
                'vote_average' => 8.4,
                'vote_count' => 34000,
                'popularity' => 145.5,
                'budget' => 165000000,
                'revenue' => 701729206,
                'genres' => [
                    ['id' => 878, 'name' => 'Bilim Kurgu'],
                    ['id' => 18, 'name' => 'Drama'],
                    ['id' => 53, 'name' => 'Gerilim']
                ],
                'credits' => [
                    'cast' => [
                        ['id' => 10296, 'name' => 'Matthew McConaughey', 'character' => 'Cooper', 'order' => 0, 'profile_path' => '/m.jpg'],
                        ['id' => 1813, 'name' => 'Anne Hathaway', 'character' => 'Brand', 'order' => 1, 'profile_path' => '/a.jpg']
                    ],
                    'crew' => [
                        ['id' => 525, 'name' => 'Christopher Nolan', 'department' => 'Directing', 'job' => 'Director']
                    ]
                ],
                'production_companies' => [
                    ['id' => 923, 'name' => 'Legendary Pictures', 'logo_path' => '/l.png', 'origin_country' => 'US']
                ]
            ];
        }

        if (strpos($endpoint, '/person/') === 0) {
            return [
                'id' => 525,
                'name' => 'Christopher Nolan',
                'biography' => 'Ünlü İngiliz yönetmen, senarist ve yapımcı.',
                'birthday' => '1970-07-30',
                'place_of_birth' => 'London, England',
                'profile_path' => '/nolan.jpg',
                'known_for_department' => 'Directing',
                'popularity' => 45.2,
                'movie_credits' => [
                    'crew' => [
                        ['id' => 157336, 'title' => 'Yıldızlararası', 'release_date' => '2014-11-05', 'job' => 'Director', 'department' => 'Directing', 'vote_average' => 8.4, 'poster_path' => '/gEU2QniE6E77NI6lCU6Mxl4vO2i.jpg']
                    ]
                ]
            ];
        }

        if (strpos($endpoint, '/changes') !== false) {
            return [
                'results' => [
                    ['id' => 157336, 'adult' => false]
                ],
                'page' => 1,
                'total_pages' => 1
            ];
        }

        return ['results' => [], 'page' => 1, 'total_pages' => 1];
    }
}
