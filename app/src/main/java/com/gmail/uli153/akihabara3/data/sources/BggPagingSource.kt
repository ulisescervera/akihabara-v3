package com.gmail.uli153.akihabara3.data.sources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gmail.uli153.akihabara3.data.entities.BggItem
import com.gmail.uli153.akihabara3.data.repositories.BggRepository

class BggPagingSource<T: Any>(
    val query: String,
    val repository: BggRepository,
    val mapper: (BggItem) -> T
): PagingSource<Int, T>() {

    companion object {
        const val PAGE_SIZE = 10
        const val START = 0
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(PAGE_SIZE) ?: anchorPage?.nextKey?.minus(PAGE_SIZE)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val start = params.key ?: START
        return try {
            val response = repository.search(query, start, PAGE_SIZE)
            LoadResult.Page(
                data = response.map(mapper),
                prevKey = null,
                nextKey = if (response.size == PAGE_SIZE) start + PAGE_SIZE else null
            )
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }
}
