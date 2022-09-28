package com.gmail.uli153.akihabara3.domain.use_cases.product

import com.gmail.uli153.akihabara3.data.repositories.AkbRepository
import com.gmail.uli153.akihabara3.domain.models.Product
import com.gmail.uli153.akihabara3.domain.toTransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BuyProductUseCase(private val repository: AkbRepository) {

    suspend operator fun invoke(product: Product): Long = withContext(Dispatchers.IO) {
        return@withContext repository.addTransaction(product.toTransactionEntity())
    }
}
