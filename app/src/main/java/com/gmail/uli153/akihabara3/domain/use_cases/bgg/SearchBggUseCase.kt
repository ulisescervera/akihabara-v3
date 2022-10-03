package com.gmail.uli153.akihabara3.domain.use_cases.bgg

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gmail.uli153.akihabara3.data.repositories.BggRepository
import com.gmail.uli153.akihabara3.data.sources.BggPagingSource
import com.gmail.uli153.akihabara3.domain.models.BggSearchItem
import com.gmail.uli153.akihabara3.domain.toModel
import com.gmail.uli153.akihabara3.utils.DataWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class SearchBggUseCase(private val repository: BggRepository) {

//    operator fun invoke(query: String): Flow<DataWrapper<List<BggSearchItem>>> {
//        return flow {
//            val res = try {
//                DataWrapper.Success(repository.search(query).map { it.toModel()})
//            } catch (e: Throwable) {
//                DataWrapper.Error(e)
//            }
//            emit(res)
//        }.onStart {
//            DataWrapper.Loading
//        }.flowOn(Dispatchers.IO)
//    }

    operator fun invoke(query: String): Flow<PagingData<BggSearchItem>> {
        return Pager(
            config = PagingConfig(pageSize = BggPagingSource.PAGE_SIZE),
            pagingSourceFactory = { BggPagingSource(query, repository) { it.toModel() } }
        ).flow.flowOn(Dispatchers.IO)
    }
}
