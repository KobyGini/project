package com.ironsource.project.domain.repository

import androidx.paging.PagingData
import com.ironsource.project.domain.models.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

     fun getPopularMovies(): Flow<PagingData<Movie>>
}