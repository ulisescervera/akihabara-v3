package com.gmail.uli153.akihabara3.domain.use_case.transaction

data class TransactionsUseCases(
    val getBalanceUseCase: GetBalanceUseCase,
    val getTransactionsUseCase: GetTransactionsUseCase,
    val addBalanceUseCase: AddBalanceUseCase,
    val deleteTransactionUseCase: DeleteTransactionUseCase
)
