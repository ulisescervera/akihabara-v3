package com.gmail.uli153.akihabara3.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

enum class ProductType {
    DRINK, FOOD
}

@Entity
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val type: ProductType,
    val name: String,
    val price: BigDecimal,
    @ColumnInfo(name = "default_image") val defaultImage: Int?,
    @ColumnInfo(name = "local_image") val localImage: String?,
    val favorite: Boolean = false
)