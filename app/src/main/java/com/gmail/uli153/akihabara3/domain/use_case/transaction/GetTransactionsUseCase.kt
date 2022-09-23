package com.gmail.uli153.akihabara3.domain.use_case.transaction

import com.gmail.uli153.akihabara3.data.models.Transaction
import com.gmail.uli153.akihabara3.data.repositories.AkbRepository
import com.gmail.uli153.akihabara3.utils.DataWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GetTransactionsUseCase(private val repository: AkbRepository) {

    operator fun invoke(): Flow<DataWrapper<List<Transaction>>> {
        return repository.fetchTransactions()
            .onStart { DataWrapper.Loading }
            .map { DataWrapper.Success(it) }
    }
}
