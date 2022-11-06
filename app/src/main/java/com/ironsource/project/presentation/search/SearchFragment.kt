package com.ironsource.project.presentation.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import com.ironsource.project.R
import com.ironsource.project.databinding.FragmentSearchBinding
import com.ironsource.project.presentation.common.adapters.MovieListPagingAdapter
import com.ironsource.project.presentation.util.ext.hideKeyboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!

    private lateinit var searchViewModel: SearchViewModel

    private val adapter: MovieListPagingAdapter = MovieListPagingAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMembers()
        collectUiState()
        collectAdapterLoadStateFlow()
    }

    private fun collectUiState() {
        lifecycleScope.launch {
            searchViewModel.pagingDataFlow.distinctUntilChanged()
                .collectLatest {
                    adapter.submitData(it)
                }
        }
    }

    private fun initMembers() {
        searchViewModel = ViewModelProvider(requireActivity()).get(SearchViewModel::class.java)

        binding.apply {

            binding.searchMovieListLayout.movieListRecyclerView.adapter = adapter

            searchRepo.setText(searchViewModel.getQuery())

            searchRepo.setOnEditorActionListener { tv, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    searchMovie()
                    hideKeyboard()
                    true
                } else {
                    false
                }
            }

            searchRepo.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    searchMovie()
                    hideKeyboard()
                    true
                } else {
                    false
                }
            }

            binding.searchMovieListLayout.retryButton.setOnClickListener { adapter.retry() }

        }
    }

    private fun collectAdapterLoadStateFlow() {
        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->

                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0

                binding.apply {
                    // show empty list
                    searchMovieListLayout.movieListEmptyTextView.isVisible = isListEmpty
                    // Only show the list if refresh succeeds, either from the the local db or the remote.
                    searchMovieListLayout.movieListRecyclerView.isVisible =
                        loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                    // Show loading spinner during initial load or refresh.
                    searchMovieListLayout.movieListProgressBar.isVisible =
                        loadState.mediator?.refresh is LoadState.Loading
                    // Show the retry state if initial load or refresh fails.
                    searchMovieListLayout.retryButton.isVisible =
                        loadState.mediator?.refresh is LoadState.Error && adapter.itemCount == 0

                }

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

    private fun searchMovie() {
        binding.searchRepo.text.trim().toString().let {
            searchViewModel.searchMovieByQuery(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}