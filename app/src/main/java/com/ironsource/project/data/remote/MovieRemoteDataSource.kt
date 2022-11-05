package com.ironsource.project.data.remote

import com.ironsource.project.domain.util.MovieConstants.INCLUDE_ADULTS
import com.ironsource.project.domain.util.MovieConstants.INCLUDE_VIDEO
import com.ironsource.project.domain.util.MovieConstants.SORT_BY_POPULARITY
import com.ironsource.project.domain.util.MovieConstants.WITH_WATCH_MONETIZATION_TYPES
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieRemoteDataSource {

    /*  @GET("/movie/{movieId}")
      suspend fun searchForMovie(
          @Path("movieId") movieId : Int ,
          @Query("api_key") api:String = BuildConfig.MOVIE_API_KEY
      ):Response<Movie>

  */

    @GET("movie/popular/")
    suspend fun getPopularMoviesList(
        @Query("page") page: Int
    ): MovieResponse

    @GET("discover/movie/")
    suspend fun getDiscoverMoviesList(
        @Query("sort_by") sortBy:String = SORT_BY_POPULARITY,
        @Query("include_adult") includeAdult:String = INCLUDE_ADULTS,
        @Query("include_video") includeVideo:String = INCLUDE_VIDEO,
        @Query("page") page:Int,
        @Query("with_watch_monetization_types") with_watch_monetization_types:String = WITH_WATCH_MONETIZATION_TYPES,
    ): MovieResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int
    ): MovieResponse

    @GET("search/movie")
    suspend fun getSearchedMovies(
        @Query("query") query: String,
        @Query("page") page: Int? = null,
        @Query("include_adult") includeAdult:String = INCLUDE_ADULTS,
    ): MovieResponse

    @GET("trending/{media_type}/{time_window}")
    suspend fun getTrendingMovies(
        @Path("media_type") mediaType : String = MediaType.MOVIE,
        @Path("time_window") timeWindow : String = TimeWindow.WEEK,
    ): MovieResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovieListMovies(
        @Query("page") page: Int,
    ): MovieResponse

    @GET("trending/{media_type}/{time_window}")
    suspend fun getDailyTrendingMovies(
        @Path("media_type") mediaType : String = MediaType.MOVIE,
        @Path("time_window") timeWindow : String = TimeWindow.DAY,
    ): MovieResponse

    object MediaType{
        const val ALL = "all"
        const val MOVIE = "movie"
        const val TV = "tv"
        const val PERSON = "person"
    }

    object TimeWindow {
        const val DAY = "day"
        const val WEEK = "week"
    }

}