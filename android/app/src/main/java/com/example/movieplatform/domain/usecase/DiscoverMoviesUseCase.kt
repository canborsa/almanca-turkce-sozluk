package com.example.movieplatform.domain.usecase

import com.example.movieplatform.domain.model.Movie
import com.example.movieplatform.domain.repository.MovieRepository

class DiscoverMoviesUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(
        genres: String? = null,
        yearMin: Int? = null,
        yearMax: Int? = null,
        ratingMin: Float? = null,
        votesMin: Int? = null,
        durationMin: Int? = null,
        durationMax: Int? = null,
        sortBy: String? = "popularity"
    ): Result<List<Movie>> {
        return repository.discoverMovies(genres, yearMin, yearMax, ratingMin, votesMin, durationMin, durationMax, sortBy)
    }
}
