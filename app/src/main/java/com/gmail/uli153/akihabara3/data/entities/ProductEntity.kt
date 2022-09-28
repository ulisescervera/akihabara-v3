package com.gmail.uli153.akihabara3.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.domain.models.Nameable
import java.math.BigDecimal

enum class ProductType(val nameResId: Int) {
    DRINK(R.string.drinks),
    FOOD(R.string.foods)
}

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: ProductType,
    val name: String,
    val price: BigDecimal,
    @ColumnInfo(name = "default_image") val defaultImage: Int,
    @ColumnInfo(name = "custom_image") val customImage: String,
    val favorite: Boolean = false
)
