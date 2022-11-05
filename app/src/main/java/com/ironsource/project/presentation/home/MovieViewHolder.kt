package com.ironsource.project.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ironsource.project.R
import com.ironsource.project.databinding.ItemMovieListBinding
import com.ironsource.project.domain.models.Movie
import com.ironsource.project.domain.util.MovieConstants

class MovieViewHolder(
    private val binding: ItemMovieListBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: Movie?, onMovieClick: View.OnClickListener?) {
        binding.movieImage.load(
            MovieConstants.IMAGE_BASE_URL + movie?.imageUrl
        ) {
            placeholder(R.drawable.ic_baseline_movie)
        }

        binding.root.setOnClickListener {
            onMovieClick?.onClick(it)
        }
    }

    companion object {
        fun create(viewGroup: ViewGroup): MovieViewHolder {
            val binding = ItemMovieListBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
            return MovieViewHolder(
                binding
            )
        }
    }
}