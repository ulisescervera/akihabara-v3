package com.gmail.uli153.akihabara3.domain.use_cases.product

import com.gmail.uli153.akihabara3.data.repositories.AkbRepository
import com.gmail.uli153.akihabara3.domain.models.Product
import com.gmail.uli153.akihabara3.domain.toEntity

class CreateProductUseCase(private val repository: AkbRepository) {

    suspend operator fun invoke(product: Product) {
        repository.addProduct(product.toEntity())
    }
}
