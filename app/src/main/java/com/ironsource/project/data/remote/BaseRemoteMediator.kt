package com.ironsource.project.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.ironsource.project.data.local.MovieDatabase
import com.ironsource.project.data.local.RemoteKeys
import com.ironsource.project.domain.models.Movie
import com.ironsource.project.domain.util.MovieConstants

@OptIn(ExperimentalPagingApi::class)
abstract class BaseRemoteMediator(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieDatabase: MovieDatabase,
    private val query: String = "",
) : RemoteMediator<Int, Movie>(), IRemoteMediator {

    override suspend fun initialize(): InitializeAction {
        // Require that remote REFRESH is launched on initial load and succeeds before launching
        // remote PREPEND / APPEND.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, Movie>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: MovieConstants.DEFAULT_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {

                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        return handleRequest(page,loadType)
    }

    /**
     * get the last remote key inserted which had the data
     */
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Movie>): RemoteKeys? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { doggo -> movieDatabase.remoteKeysDao().remoteKeysMovieId(doggo.id) }
    }

    /**
     * get the first remote key inserted which had the data
     */
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Movie>): RemoteKeys? {
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let { doggo -> movieDatabase.remoteKeysDao().remoteKeysMovieId(doggo.id) }
    }

    /**
     * get the closest remote key inserted which had the data
     */
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Movie>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                movieDatabase.remoteKeysDao().remoteKeysMovieId(repoId)
            }
        }
    }

}

@OptIn(ExperimentalPagingApi::class)
interface IRemoteMediator {
    suspend fun handleRequest(
        page: Int,
        loadType: LoadType
    ): RemoteMediator.MediatorResult
}