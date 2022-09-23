package com.gmail.uli153.akihabara3.domain.use_case.transaction

import androidx.lifecycle.viewModelScope
import com.gmail.uli153.akihabara3.data.models.Transaction
import com.gmail.uli153.akihabara3.data.models.TransactionType
import com.gmail.uli153.akihabara3.data.repositories.AkbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.*

class AddBalanceUseCase(private val repository: AkbRepository) {

    suspend operator fun invoke(amount: BigDecimal, title: String) {
        val transaction = Transaction(
            type = TransactionType.BALANCE,
            date = Date(),
            title = title,
            amount = amount,
            defaultImage = 0,
            customImage = null
        )
        repository.addTransaction(transaction)
    }
}
