package com.gmail.uli153.akihabara3.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File
import java.math.BigDecimal
import java.util.*

enum class TransactionType {
    BUY, BALANCE
}

@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: TransactionType,
    val date: Date?, // Puede fallar en el parseo desde la base de datos
    val title: String?,
    val amount: BigDecimal,
    @ColumnInfo(name = "default_image") val defaultImage: Int,
    @ColumnInfo(name = "custom_image") val customImage: File?,
)