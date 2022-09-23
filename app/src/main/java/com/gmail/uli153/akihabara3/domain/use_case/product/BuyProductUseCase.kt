package com.gmail.uli153.akihabara3.domain.use_case.product

import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.data.models.Transaction
import com.gmail.uli153.akihabara3.data.models.TransactionType
import com.gmail.uli153.akihabara3.data.repositories.AkbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class BuyProductUseCase(private val repository: AkbRepository) {

    suspend operator fun invoke(product: Product): Long = withContext(Dispatchers.IO) {
        val transaction = Transaction(
            type = TransactionType.BUY,
            date = Date(),
            title = product.name,
            amount = product.price,
            customImage = product.customImage,
            defaultImage = product.defaultImage
        )
        return@withContext repository.addTransaction(transaction)
    }
}
