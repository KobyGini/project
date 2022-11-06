package com.ironsource.project.di

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.room.Room
import com.ironsource.project.data.local.MovieDatabase
import com.ironsource.project.data.remote.MovieRemoteDataSource
import com.ironsource.project.data.repository.MovieRepositoryImpl
import com.ironsource.project.domain.repository.MovieRepository
import com.ironsource.project.domain.usecases.GetPopularMovieUseCase
import com.ironsource.project.domain.usecases.SearchMovieUseCase
import com.ironsource.project.domain.util.MovieConstants.HTTP_API_KEY
import com.ironsource.project.domain.util.MovieConstants.MOVIE_DB
import com.ironsource.project.domain.util.MovieConstants.PEXELS_SERVICE
import com.ironsource.project.domain.util.MovieConstants.TMDB_API_ENDPOINT
import com.ironsource.project.domain.util.MovieConstants.TMDB_API_KEY
import com.ironsource.project.domain.util.MovieConstants.TMDB_RETROFIT
import com.ironsource.project.domain.util.MovieConstants.TMDB_SERVICE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import javax.inject.Named
import javax.inject.Singleton

@ExperimentalPagingApi
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMovieDatabase(
        @ApplicationContext context: Context
    ) = Room
        .databaseBuilder(
            context.applicationContext,
            MovieDatabase::class.java,
            MOVIE_DB
        )
        .fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun provideMovieRemoteDataSource(
        retrofit: Retrofit
    ): MovieRemoteDataSource {
        return retrofit.create(MovieRemoteDataSource::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(TMDB_API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideOksHttpClient(
        okHttpLogger: HttpLoggingInterceptor,
        apiKeyInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(okHttpLogger)
            .build()
    }

    @ExperimentalPagingApi
    @Singleton
    @Provides
    fun provideOkHttpNetworkInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request();
            val url = request.url.newBuilder()
                .addQueryParameter(HTTP_API_KEY, TMDB_API_KEY)
                .build();
            val newRequest = request.newBuilder().url(url).build();
            chain.proceed(newRequest)
        }
    }

    @ExperimentalPagingApi
    @Singleton
    @Provides
    fun provideHttpLogger(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    @ExperimentalPagingApi
    @Singleton
    @Provides
    fun provideDoggoImagesRepository(
        movieRemoteDataSource: MovieRemoteDataSource,
        movieDatabase: MovieDatabase
    ): MovieRepository = MovieRepositoryImpl(
        movieRemoteDataSource,
        movieDatabase
    )

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO

    @Singleton
    @Provides
    fun provideGetPopularMovieUseCase(
        repository: MovieRepository
    ) = GetPopularMovieUseCase(repository)

    @Singleton
    @Provides
    fun provideSearchMovieUseCase(
        repository: MovieRepository
    ) = SearchMovieUseCase(repository)
}