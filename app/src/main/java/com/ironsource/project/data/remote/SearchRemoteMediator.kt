package com.ironsource.project.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.room.withTransaction
import com.ironsource.project.data.local.MovieDatabase
import com.ironsource.project.data.local.RemoteKeys
import com.ironsource.project.domain.models.Movie
import com.ironsource.project.domain.util.MovieConstants
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
class SearchRemoteMediator(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieDatabase: MovieDatabase,
    private val query: String = "",
) : BaseRemoteMediator(movieRemoteDataSource, movieDatabase) {

    override suspend fun handleRequest(
        page: Int,
        loadType: LoadType
    ): MediatorResult {
        try {

            val response = movieRemoteDataSource.getSearchedMovies(
                page = page,
                query = query
            ).results

            val isEndOfList = response.isEmpty()
            movieDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    movieDatabase.remoteKeysDao().clearRemoteKeys()
                    movieDatabase.movieDao().deleteSearchMovies()
                }

                val prevKey = if (page == MovieConstants.DEFAULT_PAGE_INDEX) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keys = response.map {
                    RemoteKeys(movieId = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                val newList: List<Movie> =
                    response.map { it.copy(isSearched = true) }

                movieDatabase.remoteKeysDao().insertAll(keys)
                movieDatabase.movieDao().insertAll(newList)
            }
            return MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }

    }

}