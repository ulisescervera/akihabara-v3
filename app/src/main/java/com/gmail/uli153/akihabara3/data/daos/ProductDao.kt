package com.gmail.uli153.akihabara3.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.gmail.uli153.akihabara3.data.entities.ProductEntity
import com.gmail.uli153.akihabara3.data.entities.ProductType
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM product ORDER BY name ASC")
    fun getAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM product WHERE type LIKE :type ORDER BY name ASC")
    fun getDrikns(type: ProductType = ProductType.DRINK): Flow<List<ProductEntity>>

    @Query("SELECT * FROM product WHERE type LIKE :type ORDER BY name ASC")
    fun getFoods(type: ProductType = ProductType.FOOD): Flow<List<ProductEntity>>

    @Query("SELECT * FROM product WHERE name LIKE :name LIMIT 1")
    suspend fun findByName(name: String): ProductEntity?

    @Query("SELECT * FROM product WHERE id LIKE :id LIMIT 1")
    suspend fun get(id: Long): ProductEntity?

    @Insert(onConflict = REPLACE)
    suspend fun insert(productEntity: ProductEntity)

    @Delete
    suspend fun delete(productEntity: ProductEntity)

}
