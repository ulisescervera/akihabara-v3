package com.gmail.uli153.akihabara3.domain.use_case.transaction

import com.gmail.uli153.akihabara3.data.models.Transaction
import com.gmail.uli153.akihabara3.data.models.TransactionType
import com.gmail.uli153.akihabara3.data.repositories.AkbRepository
import com.gmail.uli153.akihabara3.utils.DataWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal

class GetBalanceUseCase(private val repository: AkbRepository) {

    operator fun invoke(): Flow<BigDecimal> {
        return repository.fetchTransactions().map { transactions ->
            transactions.sumOf { if (it.type == TransactionType.BUY) it.amount.negate() else it.amount }
        }
    }
}
