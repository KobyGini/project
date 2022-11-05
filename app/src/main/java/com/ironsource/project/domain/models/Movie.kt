package com.ironsource.project.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ironsource.project.domain.util.MovieConstants.MOVIE_TABLE
import java.io.Serializable

@Entity(tableName = MOVIE_TABLE)
data class Movie(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    val title: String? = null,
    @SerializedName("release_date")
    val releaseDate: String? = null,
    @SerializedName("poster_path")
    var imageUrl: String? = null,
    @SerializedName("vote_average")
    val voteAverage: Double,
    val video: Boolean = false,

    //My Fields
    var isFavourite: Boolean = false,
    var isPopular: Boolean = false,
    var isDiscover: Boolean = false,
    var isSearched: Boolean = false
) : Serializable