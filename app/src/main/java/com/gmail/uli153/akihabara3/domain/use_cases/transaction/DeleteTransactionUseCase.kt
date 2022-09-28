package com.gmail.uli153.akihabara3.domain.use_cases.transaction

import com.gmail.uli153.akihabara3.data.repositories.AkbRepository

class DeleteTransactionUseCase(private val repository: AkbRepository) {

    suspend operator fun invoke(transactionId: Long) {
        repository.deleteTransaction(transactionId)
    }
}
