package com.gmail.uli153.akihabara3.domain.models

import com.gmail.uli153.akihabara3.data.entities.TransactionType
import java.io.File
import java.math.BigDecimal
import java.util.*

data class Transaction(
    val id: Long = 0,
    val type: TransactionType,
    val date: Date,
    val title: String,
    val amount: BigDecimal,
    val defaultImage: Int,
    val customImage: File?,
): Nameable {
    override val name: String = title
}
