package com.ironsource.project.data.remote

import com.google.gson.annotations.SerializedName
import com.ironsource.project.domain.models.Movie

data class MovieResponse(
    var page : Int,
    var results : List<Movie>,
    @SerializedName("total_pages")
    var totalPage: Int,
    @SerializedName("total_results")
    var totalResults : Int
)
