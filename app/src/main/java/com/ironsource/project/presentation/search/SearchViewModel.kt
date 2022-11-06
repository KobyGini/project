package com.ironsource.project.presentation.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ironsource.project.domain.models.Movie
import com.ironsource.project.domain.repository.MovieRepository
import com.ironsource.project.domain.usecases.SearchMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@HiltViewModel
class SearchViewModel
@Inject constructor(
    private val searchMovieUseCase: SearchMovieUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        const val KEY_MOVIE_QUERY = "query"
        const val DEFAULT_MOVIE_QUERY = ""
    }

    val pagingDataFlow: Flow<PagingData<Movie>> = savedStateHandle.getLiveData<String>(KEY_MOVIE_QUERY)
        .asFlow()
        .distinctUntilChanged()
        .flatMapLatest {
            searchMovieUseCase.invoke(query = it)
        }.cachedIn(viewModelScope)

    init {
        if (!savedStateHandle.contains(KEY_MOVIE_QUERY)) {
            savedStateHandle[KEY_MOVIE_QUERY] = DEFAULT_MOVIE_QUERY
        }
    }

    fun getQuery() = savedStateHandle.get<String>(KEY_MOVIE_QUERY)

    fun searchMovieByQuery(query: String) {
        if (query.isNotEmpty() && savedStateHandle.get<String>(KEY_MOVIE_QUERY) != query) {
            savedStateHandle[KEY_MOVIE_QUERY] = query
        }
    }

}