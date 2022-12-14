package com.ironsource.project.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ironsource.project.data.local.MovieDatabase
import com.ironsource.project.data.local.RemoteKeys
import com.ironsource.project.domain.models.Movie
import com.ironsource.project.domain.util.MovieConstants
import com.ironsource.project.domain.util.MovieConstants.DEFAULT_PAGE_INDEX
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
class MovieRemoteMediator(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieDatabase: MovieDatabase,
) : BaseRemoteMediator(movieRemoteDataSource, movieDatabase) {

    override suspend fun handleRequest(page: Int, loadType: LoadType): MediatorResult {

        try {
            val response =
                movieRemoteDataSource.getPopularMoviesList(
                    page = page,
                ).results

            val isEndOfList = response.isEmpty()
            movieDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    movieDatabase.remoteKeysDao().clearRemoteKeys()
                    movieDatabase.movieDao().deleteDiscoverMovies()
                }

                val prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keys = response.map {
                    RemoteKeys(movieId = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                movieDatabase.remoteKeysDao().insertAll(keys)
                movieDatabase.movieDao().insertAll(response)
            }
            return MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }
}

