package com.ironsource.project.data.repository

import com.ironsource.project.data.remote.MovieRemoteDataSource
import com.ironsource.project.domain.repository.MovieRepository


import androidx.paging.*
import com.ironsource.project.data.local.MovieDatabase
import com.ironsource.project.data.remote.MovieRemoteMediator
import com.ironsource.project.domain.models.Movie
import com.ironsource.project.domain.util.MovieConstants.DEFAULT_PAGE_SIZE

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.random.Random

/**
 * repository class to manage the data flow and map it if needed
 */

const val TAG = "MovieRepository"

@ExperimentalPagingApi
class MovieRepositoryImpl
@Inject constructor(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieDatabase: MovieDatabase
) : MovieRepository {

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = DEFAULT_PAGE_SIZE, enablePlaceholders = true)
    }

    fun letDiscoverMoveListFlowDb(): Flow<PagingData<Movie>> {

        val pagingSourceFactory = { movieDatabase.movieDao().getDiscoverMovieList() }
        return Pager(
            config = getDefaultPageConfig(),
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = MovieRemoteMediator(
                movieRemoteDataSource,
                movieDatabase,
            )
        ).flow
    }

    override fun getPopularMovies(): Flow<PagingData<Movie>> {
        val pagingSourceFactory = { movieDatabase.movieDao().getPopularMovieList() }
        return Pager(
            config = getDefaultPageConfig(),
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = MovieRemoteMediator(
                movieRemoteDataSource,
                movieDatabase,
            )
        ).flow
    }

//    fun letSearchMovieFlowDb(query: String): Flow<PagingData<Movie>> {
//
//        val pagingSourceFactory = { movieDatabase.movieDao().getSearchedMovieList() }
//        return Pager(
//            config = getDefaultPageConfig(),
//            pagingSourceFactory = pagingSourceFactory,
//            remoteMediator = SearchRemoteMediator(
//                movieRemoteDataSource,
//                movieDatabase,
//                query
//            )
//        ).flow
//    }

//    fun letCategorisedMovieFlowDb() = flow<Result<List<MovieCategory>>> {
//
//        val categoryMovieList = ArrayList<MovieCategory>()
//
//        categoryMovieList.add(
//            MovieCategory(context.getString(R.string.now_in_cinemas)).also {
//                it.result.addAll(movieRemoteDataSource.getNowPlayingMovieListMovies(page = 1).results.toList())
//            })
//
//        categoryMovieList.add(
//            MovieCategory(context.getString(R.string.trending)).also {
//                it.result.addAll(movieRemoteDataSource.getTrendingMovies().results.toList())
//            })
//
//        categoryMovieList.add(
//            MovieCategory(context.getString(R.string.popular)).also {
//                it.result.addAll(movieRemoteDataSource.getPopularMoviesList(page = 1).results.toList())
//            })
//
//        categoryMovieList.add(
//            MovieCategory(context.getString(R.string.top_rated)).also {
//                it.result.addAll(movieRemoteDataSource.getTopRatedMovies(page = 1).results.toList())
//            })
//
//        if (categoryMovieList.isEmpty()) {
//            d(TAG, "Finish Failed")
//            emit(Result.Error)
//        } else {
//            d(TAG, "Finish Successfully")
//
//        }
//        emit(Result.Success(categoryMovieList))
//
//    }

//    fun letMovieTrailersFlowDb(movieId: Int) = flow {
//        if (movieId != 0) {
//            val res = movieRemoteDataSource.getMovieDetailsAndTrailers(movieId = movieId)
//            val res2 = imdbService.getImdbRating(movieId = res.imdbId)
//            res.imdbRating = res2.rating
//            d(TAG,"IMDB RATING = ${res.imdbRating}")
//            emit(res)
//        } else {
//            emit(null)
//        }
//    }

    fun letDailyTrendMovieFlowDb() = flow {

        val res = movieRemoteDataSource.getDailyTrendingMovies()
        emit(res.results[Random.nextInt(0, 19)])
//         val video = res.results.map {
//             it.video
//         }
//
//        if (video.isNotEmpty()){
//            res.results.forEach{
//                if (it.video) emit(it)
//            }
//        }else(emit(res.results[0]))
    }

}