package com.gmail.uli153.akihabara3.data.daos

import androidx.room.*
import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.data.models.ProductType
import com.gmail.uli153.akihabara3.data.models.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM `transaction`")
    fun getAll(): Flow<List<Transaction>>

    @Query("SELECT * FROM `transaction` WHERE id LIKE :id LIMIT 1")
    suspend fun get(id: Long): Transaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("DELETE FROM `transaction` WHERE id LIKE :id ")
    suspend fun delete(id: Long)

}