package com.ironsource.project.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ironsource.project.domain.models.Movie
import com.ironsource.project.domain.usecases.GetPopularMovieUseCase
import com.ironsource.project.domain.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel
@Inject constructor(
    private val getPopularMovieUseCase: GetPopularMovieUseCase,
) : ViewModel() {

    val uiState: StateFlow<MovieListUiState> =
        getPopularMovieUseCase
            .invoke()
            .map {
                MovieListUiState(data = it)
            }.stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = MovieListUiState()
            )


    data class MovieListUiState(
        val data: PagingData<Movie>? = null,
    )
}