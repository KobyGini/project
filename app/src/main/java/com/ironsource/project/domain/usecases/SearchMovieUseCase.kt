package com.ironsource.project.domain.usecases

import com.ironsource.project.domain.repository.MovieRepository
import javax.inject.Inject

class SearchMovieUseCase
@Inject constructor(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(query: String) = movieRepository.searchMovie(query)
}