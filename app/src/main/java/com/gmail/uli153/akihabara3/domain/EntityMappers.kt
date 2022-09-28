package com.gmail.uli153.akihabara3.domain

import com.gmail.uli153.akihabara3.data.converters.Converters
import com.gmail.uli153.akihabara3.data.entities.ProductEntity
import com.gmail.uli153.akihabara3.data.entities.TransactionEntity
import com.gmail.uli153.akihabara3.data.entities.TransactionType
import com.gmail.uli153.akihabara3.domain.models.Product
import com.gmail.uli153.akihabara3.domain.models.Transaction
import java.util.*

private val converters = Converters()

fun ProductEntity.toModel(): Product {
    return Product(
        id,
        type,
        name,
        price,
        defaultImage,
        converters.fromString(customImage),
        favorite
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id,
        type,
        name,
        price,
        defaultImage,
        converters.fromFile(customImage),
        favorite
    )
}

fun TransactionEntity.toModel(): Transaction {
    return Transaction(
        id,
        type,
        date,
        title,
        amount,
        defaultImage,
        converters.fromString(customImage),
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id,
        type,
        date,
        title,
        amount,
        defaultImage,
        converters.fromFile(customImage)
    )
}

fun Product.toTransactionEntity(): TransactionEntity {
    return TransactionEntity(
        type = TransactionType.BUY,
        date = Date(),
        title = name,
        amount = price,
        defaultImage = defaultImage,
        customImage = converters.fromFile(customImage),
    )
}
