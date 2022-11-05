package com.ironsource.project.domain.util

object MovieConstants {

    //REMOTE MEDIATOR
    const val DEFAULT_PAGE_INDEX = 1
    const val DEFAULT_PAGE_SIZE = 4

    //Database
    const val MOVIE_DB = "movie.db"
    const val MOVIE_TABLE = "movie_table"
    const val SEARCH_REMOTE_KEYS_TABLE = "search_remote_Keys"
    const val REMOTE_KEYS_TABLE = "remotekeys"

    const val HTTP_API_KEY="api_key"
    //THE MOVIE DB
    const val TMDB_API_KEY = "cbc8ee7aac105c070f5f830d4e2c2f0b"
    const val TMDB_API_ENDPOINT = "https://api.themoviedb.org/3/"
    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/"
    const val SORT_BY_POPULARITY = "popularity.desc"
    const val INCLUDE_ADULTS = "false"
    const val INCLUDE_VIDEO = "false"
    const val WITH_WATCH_MONETIZATION_TYPES = "flatrate"

    //IMDB API
    const val IMDB_API_KEY = "k_w54sy5m8"
    const val IMDB_BASE_URL = "https://imdb-api.com/en/API/"
    const val PEXELS_URL = "https://api.pexels.com/v1/"

    //YOUTUBE API KEY
    const val GOOGLE_API_KEY = "AIzaSyAoK4OFttmJJTwMGmjgPXGonQwRzm8QU3g"


    //    const val HEADER_API_KEY = "x-api-key"


    //Hilt
    const val IMDB_RETROFIT = "imdb_retrofit"
    const val TMDB_RETROFIT = "tmdb_retrofit"
    const val PEXELS_RETROFIT = "pexels_retrofit"

    const val IMDB_SERVICE = "imdb_service"
    const val TMDB_SERVICE = "tmdb_service"
    const val PEXELS_SERVICE = "pexels_service"



}