package com.gmail.uli153.akihabara3.domain.use_cases.bgg

import com.gmail.uli153.akihabara3.data.repositories.BggRepository
import com.gmail.uli153.akihabara3.domain.models.BggSearchItem
import com.gmail.uli153.akihabara3.domain.toModel
import com.gmail.uli153.akihabara3.utils.DataWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

class SearchBggUseCase(private val repository: BggRepository) {

    operator fun invoke(query: String): Flow<DataWrapper<List<BggSearchItem>>> {
        return flow {
            val res = try {
                DataWrapper.Success(repository.search(query).map { it.toModel()})
            } catch (e: Throwable) {
                DataWrapper.Error(e)
            }
            emit(res)
        }.onStart {
            DataWrapper.Loading
        }.flowOn(Dispatchers.IO)
    }
}
