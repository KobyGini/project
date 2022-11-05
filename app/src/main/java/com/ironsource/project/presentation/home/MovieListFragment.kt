package com.ironsource.project.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import com.ironsource.project.R
import com.ironsource.project.databinding.FragmentMovieListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

@AndroidEntryPoint
class MovieListFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null

    private val adapter = MovieListPagingAdapter()

    private val binding get() = _binding!!

    private val viewModel: MovieListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMembers()

        collectUiState()

        collectAdapterLoadStateFlow()
    }

    private fun initMembers() {
        adapter.setOnMovieClickListener {

        }
        binding.movieListRecyclerView.adapter = adapter
        binding.retryButton.setOnClickListener { adapter.retry() }
    }

    private fun collectUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                it.data?.let { adapter.submitData(it) }
            }
        }
    }

    private fun collectAdapterLoadStateFlow() {
        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->

                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0

                // show empty list
                binding.movieListEmptyTextView.isVisible = isListEmpty
                // Only show the list if refresh succeeds, either from the the local db or the remote.
                binding.movieListRecyclerView.isVisible =
                    loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                // Show loading spinner during initial load or refresh.
                binding.movieListProgressBar.isVisible =
                    loadState.mediator?.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                binding.retryButton.isVisible =
                    loadState.mediator?.refresh is LoadState.Error && adapter.itemCount == 0

                handleError(loadState)
            }
        }
    }

    private suspend fun handleError(loadState: CombinedLoadStates) {
        val errorState = loadState.source.append as? LoadState.Error
            ?: loadState.source.prepend as? LoadState.Error
            ?: loadState.source.refresh as? LoadState.Error
            ?: loadState.append as? LoadState.Error
            ?: loadState.prepend as? LoadState.Error
            ?: loadState.refresh as? LoadState.Error

        if (errorState is LoadState.Error) {
            withContext(Dispatchers.Main) {
                val message = when (errorState.error) {
                    is IOException -> getString(R.string.general_error)
                    is HttpException -> getString(R.string.network_error)
                    else -> getString(R.string.general_error)
                }

                Snackbar.make(
                    binding.root,
                    getString(
                        R.string.error_message,
                        message
                    ),
                    Snackbar.LENGTH_SHORT
                ).show()

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}