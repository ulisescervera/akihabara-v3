package com.gmail.uli153.akihabara3.data.repositories

import com.gmail.uli153.akihabara3.data.AkihabaraDatabase
import com.gmail.uli153.akihabara3.data.entities.ProductEntity
import com.gmail.uli153.akihabara3.data.entities.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AkbRepositoryImpl(private val db: AkihabaraDatabase): AkbRepository {

    override fun fetchProducts(): Flow<List<ProductEntity>> {
        return db.productDao().getAll()
    }

    override fun fetchDriks(): Flow<List<ProductEntity>> {
        return db.productDao().getDrikns()
    }

    override fun fetchFoods(): Flow<List<ProductEntity>> {
        return db.productDao().getFoods()
    }

    override fun fetchTransactions(): Flow<List<TransactionEntity>> {
        return db.transactionDao().getAll()
    }

    override suspend fun deleteProduct(productEntity: ProductEntity) = withContext(Dispatchers.IO) {
        db.productDao().delete(productEntity)
    }

    override suspend fun addProduct(productEntity: ProductEntity) = withContext(Dispatchers.IO) {
        db.productDao().insert(productEntity)
    }

    override suspend fun addTransaction(transactionEntity: TransactionEntity) = withContext(Dispatchers.IO) {
        db.transactionDao().insert(transactionEntity)
    }

    override suspend fun deleteTransaction(transactionEntity: TransactionEntity) = withContext(Dispatchers.IO) {
        db.transactionDao().delete(transactionEntity)
    }

    override suspend fun deleteTransaction(id: Long) = withContext(Dispatchers.IO) {
        db.transactionDao().delete(id)
    }
}
