package com.gmail.uli153.akihabara3.data.repositories

import com.gmail.uli153.akihabara3.data.entities.ProductEntity
import com.gmail.uli153.akihabara3.data.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface AkbRepository {

    fun fetchProducts(): Flow<List<ProductEntity>>

    fun fetchDriks(): Flow<List<ProductEntity>>

    fun fetchFoods(): Flow<List<ProductEntity>>

    fun fetchTransactions(): Flow<List<TransactionEntity>>

    suspend fun deleteProduct(productEntity: ProductEntity)

    suspend fun addProduct(productEntity: ProductEntity)

    suspend fun addTransaction(transactionEntity: TransactionEntity): Long

    suspend fun deleteTransaction(transactionEntity: TransactionEntity)

    suspend fun deleteTransaction(id: Long)
}
