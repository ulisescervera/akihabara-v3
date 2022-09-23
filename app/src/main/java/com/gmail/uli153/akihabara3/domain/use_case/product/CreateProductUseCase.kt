package com.gmail.uli153.akihabara3.domain.use_case.product

import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.data.repositories.AkbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreateProductUseCase(private val repository: AkbRepository) {

    suspend operator fun invoke(product: Product) {
        repository.addProduct(product)
    }
}
