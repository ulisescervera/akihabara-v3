package com.gmail.uli153.akihabara3.domain.use_cases.product

import com.gmail.uli153.akihabara3.data.repositories.AkbRepository
import com.gmail.uli153.akihabara3.domain.models.Product
import com.gmail.uli153.akihabara3.domain.toModel
import com.gmail.uli153.akihabara3.utils.DataWrapper
import com.gmail.uli153.akihabara3.utils.extensions.sorted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GetFoodsUseCase(private val repository: AkbRepository) {

    operator fun invoke(): Flow<DataWrapper<List<Product>>> {
        return repository.fetchFoods()
            .onStart { DataWrapper.Loading }
            .map { DataWrapper.Success(it.map { it.toModel() }.sorted()) }
            .flowOn(Dispatchers.IO)
    }
}
