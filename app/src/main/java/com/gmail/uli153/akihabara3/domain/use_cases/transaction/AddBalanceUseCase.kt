package com.gmail.uli153.akihabara3.domain.use_cases.transaction

import com.gmail.uli153.akihabara3.data.entities.TransactionEntity
import com.gmail.uli153.akihabara3.data.entities.TransactionType
import com.gmail.uli153.akihabara3.data.repositories.AkbRepository
import java.math.BigDecimal
import java.util.*

class AddBalanceUseCase(private val repository: AkbRepository) {

    suspend operator fun invoke(amount: BigDecimal, title: String) {
        val transactionEntity = TransactionEntity(
            type = TransactionType.BALANCE,
            date = Date(),
            title = title,
            amount = amount,
            defaultImage = 0,
            customImage = ""
        )
        repository.addTransaction(transactionEntity)
    }
}
