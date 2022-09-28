package com.gmail.uli153.akihabara3.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gmail.uli153.akihabara3.domain.models.Nameable
import java.math.BigDecimal
import java.util.*

enum class TransactionType {
    BUY, BALANCE
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: TransactionType,
    val date: Date,
    val title: String,
    val amount: BigDecimal,
    @ColumnInfo(name = "default_image") val defaultImage: Int,
    @ColumnInfo(name = "custom_image") val customImage: String,
)
