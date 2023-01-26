package com.gmail.uli153.akihabara3.domain.use_cases.bgg

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gmail.uli153.akihabara3.data.DataWrapper
import com.gmail.uli153.akihabara3.data.repositories.BggRepository
import com.gmail.uli153.akihabara3.data.repositories.SearchTypes
import com.gmail.uli153.akihabara3.data.sources.BggPagingSource
import com.gmail.uli153.akihabara3.domain.models.BggSearchItem
import com.gmail.uli153.akihabara3.domain.toModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

class SearchBggUseCase(private val repository: BggRepository) {

    suspend operator fun invoke(
        query: String,
        types: Set<SearchTypes>
    ): DataWrapper<List<BggSearchItem>> {
        if (query.isBlank()) return DataWrapper.Success(emptyList())

        val t = types.ifEmpty { SearchTypes.values().toSet() }
        return try {
            delay(600)
            val res = repository.search(query, t)
            DataWrapper.Success(res.map { it.toModel() })
        } catch (e: Throwable) {
            if (e is CancellationException) throw e
            Timber.e(e)
            DataWrapper.Error(e)
        }
    }

    fun searchPaged(query: String): Flow<PagingData<BggSearchItem>> {
        return Pager(
            config = PagingConfig(pageSize = BggPagingSource.PAGE_SIZE),
            pagingSourceFactory = { BggPagingSource(query, repository) }
        ).flow.map { paging -> paging.map { entity -> entity.toModel() } }.flowOn(Dispatchers.IO)
    }

}
