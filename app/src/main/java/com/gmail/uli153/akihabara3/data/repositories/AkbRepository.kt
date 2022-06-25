package com.gmail.uli153.akihabara3.data.repositories

import com.gmail.uli153.akihabara3.AkihabaraDatabase
import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.data.models.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AkbRepository(private val db: AkihabaraDatabase) {

    fun fetchProducts(): Flow<List<Product>> {
        return db.productDao().getAll()
    }

    fun fetchDriks(): Flow<List<Product>> {
        return db.productDao().getDrikns()
    }

    fun fetchFoods(): Flow<List<Product>> {
        return db.productDao().getFoods()
    }

    fun fetchTransactions(): Flow<List<Transaction>> {
        return db.transactionDao().getAll()
    }

    suspend fun deleteProduct(product: Product) = withContext(Dispatchers.IO) {
        db.productDao().delete(product)
    }

    suspend fun addProduct(product: Product) = withContext(Dispatchers.IO) {
        db.productDao().insert(product)
    }

    suspend fun addTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        db.transactionDao().insert(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        db.transactionDao().delete(transaction)
    }

    suspend fun deleteTransaction(id: Long) = withContext(Dispatchers.IO) {
        db.transactionDao().delete(id)
    }
}