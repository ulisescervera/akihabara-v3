package com.gmail.uli153.akihabara3.domain.use_case.product

import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.data.repositories.AkbRepository
import com.gmail.uli153.akihabara3.utils.DataWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GetProductsUseCase(private val repository: AkbRepository) {

    operator fun invoke(): Flow<DataWrapper<List<Product>>> {
        return repository.fetchProducts()
            .onStart { DataWrapper.Loading }
            .map { DataWrapper.Success(it) }
    }
}
