package com.gmail.uli153.akihabara3.data.daos

import androidx.room.*
import com.gmail.uli153.akihabara3.data.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id LIKE :id LIMIT 1")
    suspend fun get(id: Long): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transactionEntity: TransactionEntity): Long

    @Delete
    suspend fun delete(transactionEntity: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id LIKE :id ")
    suspend fun delete(id: Long)

}
