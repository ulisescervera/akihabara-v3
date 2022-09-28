package com.gmail.uli153.akihabara3.data.repositories

import com.gmail.uli153.akihabara3.data.AkihabaraDatabase
import com.gmail.uli153.akihabara3.data.entities.ProductEntity
import com.gmail.uli153.akihabara3.data.entities.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AkbRepository(private val db: AkihabaraDatabase) {

    fun fetchProducts(): Flow<List<ProductEntity>> {
        return db.productDao().getAll()
    }

    fun fetchDriks(): Flow<List<ProductEntity>> {
        return db.productDao().getDrikns()
    }

    fun fetchFoods(): Flow<List<ProductEntity>> {
        return db.productDao().getFoods()
    }

    fun fetchTransactions(): Flow<List<TransactionEntity>> {
        return db.transactionDao().getAll()
    }

    suspend fun deleteProduct(productEntity: ProductEntity) = withContext(Dispatchers.IO) {
        db.productDao().delete(productEntity)
    }

    suspend fun addProduct(productEntity: ProductEntity) = withContext(Dispatchers.IO) {
        db.productDao().insert(productEntity)
    }

    suspend fun addTransaction(transactionEntity: TransactionEntity) = withContext(Dispatchers.IO) {
        db.transactionDao().insert(transactionEntity)
    }

    suspend fun deleteTransaction(transactionEntity: TransactionEntity) = withContext(Dispatchers.IO) {
        db.transactionDao().delete(transactionEntity)
    }

    suspend fun deleteTransaction(id: Long) = withContext(Dispatchers.IO) {
        db.transactionDao().delete(id)
    }
}
