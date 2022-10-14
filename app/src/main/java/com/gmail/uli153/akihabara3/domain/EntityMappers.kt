package com.gmail.uli153.akihabara3.domain

import com.gmail.uli153.akihabara3.data.converters.Converters
import com.gmail.uli153.akihabara3.data.entities.*
import com.gmail.uli153.akihabara3.domain.models.*
import com.gmail.uli153.akihabara3.domain.models.Name
import com.gmail.uli153.akihabara3.domain.models.Rank
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

fun BggItem.toModel(): BggSearchItem {
    return BggSearchItem(
        id = id,
        thumbnail = thumbnail,
        image = image,
        names = names.map { Name(NameType.valueOf(it.type.uppercase()), it.value) },
        description = description,
        yearPublished = yearpublished?.value,
        minPlayers = minplayers?.value,
        maxPlayers = maxplayers?.value,
        playingTime = playingtime?.value,
        minAge = minage?.value,
        ranks = statistics?.ratings?.ranks?.mapNotNull { it.toModel() },
        geekRating = statistics?.ratings?.bayesaverage?.value,
        rating = statistics?.ratings?.average?.value,
        weight = statistics?.ratings?.averageweight?.value,
        votes = statistics?.ratings?.usersrated?.value
    )
}

fun BggHotItemResponse.toModel(): BggHotItem {
    return BggHotItem(
        id,
        rank,
        name.value,
        thumbnail?.value,
        yearpublished?.value
    )
}

fun com.gmail.uli153.akihabara3.data.entities.Rank.toModel(): com.gmail.uli153.akihabara3.domain.models.Rank? {
    return value.toIntOrNull()?.let {
        return Rank(
            this.id,
            this.friendlyname,
            it
        )
    }
}
