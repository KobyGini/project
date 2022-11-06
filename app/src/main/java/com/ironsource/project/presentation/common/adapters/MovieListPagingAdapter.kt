package com.ironsource.project.presentation.common.adapters

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ironsource.project.domain.models.Movie

class MovieListPagingAdapter :
    PagingDataAdapter<Movie, MovieViewHolder>(MOVIE_COMPARATOR) {

    companion object {
        private val MOVIE_COMPARATOR = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie) =
                oldItem == newItem
        }
    }

    private var onMovieClick: View.OnClickListener? = null

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position), onMovieClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder.create(parent)
    }

    fun setOnMovieClickListener(
        onMovieClick: View.OnClickListener
    ) {
        this.onMovieClick = onMovieClick
    }

}