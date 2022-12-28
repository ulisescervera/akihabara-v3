package com.gmail.uli153.akihabara3.domain.use_cases.product

import com.gmail.uli153.akihabara3.data.DataWrapper
import com.gmail.uli153.akihabara3.data.repositories.AkbRepository
import com.gmail.uli153.akihabara3.domain.models.Product
import com.gmail.uli153.akihabara3.domain.toModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GetProductsUseCase(private val repository: AkbRepository) {

    operator fun invoke(): Flow<DataWrapper<List<Product>>> {
        return repository.fetchProducts()
            .onStart { DataWrapper.Loading }
            .map { DataWrapper.Success(it.map { it.toModel() }) }
            .flowOn(Dispatchers.IO)
    }
}
