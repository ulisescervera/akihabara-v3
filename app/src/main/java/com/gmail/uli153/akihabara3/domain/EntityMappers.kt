package com.gmail.uli153.akihabara3.domain

import com.gmail.uli153.akihabara3.data.converters.Converters
import com.gmail.uli153.akihabara3.data.entities.*
import com.gmail.uli153.akihabara3.domain.models.*
import com.gmail.uli153.akihabara3.domain.models.Name
import com.gmail.uli153.akihabara3.domain.models.Poll
import com.gmail.uli153.akihabara3.domain.models.PollResult
import com.gmail.uli153.akihabara3.domain.models.PollResults
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
        yearPublished = yearpublished?.value?.takeIf { it > 0 },
        minPlayers = minplayers?.value?.takeIf { it > 0 },
        maxPlayers = maxplayers?.value?.takeIf { it > 0 },
        playingTime = playingtime?.value?.takeIf { it > 0 },
        minAge = minage?.value?.takeIf { it > 0 },
        ranks = statistics?.ratings?.ranks?.mapNotNull { it.toModel() },
        geekRating = statistics?.ratings?.bayesaverage?.value?.takeIf { it > 0 },
        rating = statistics?.ratings?.average?.value?.takeIf { it > 0 },
        weight = statistics?.ratings?.averageweight?.value?.takeIf { it > 0 },
        votes = statistics?.ratings?.usersrated?.value,
        categories = links.filter { it.type == "boardgamecategory" }.map { BoardgameLink(it.id, it.value) },
        mechanics = links.filter { it.type == "boardgamemechanic" }.map { BoardgameLink(it.id, it.value) },
        polls = polls.mapNotNull { it.toModel() }
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

private fun com.gmail.uli153.akihabara3.data.entities.Rank.toModel(): com.gmail.uli153.akihabara3.domain.models.Rank? {
    return value.toIntOrNull()?.let {
        return Rank(
            this.id,
            this.friendlyname,
            it
        )
    }
}

private fun com.gmail.uli153.akihabara3.data.entities.Poll.toModel(): com.gmail.uli153.akihabara3.domain.models.Poll? {
    return Poll(
        type = PollType.values().firstOrNull { it.pollName == name } ?: return null,
        results = results.map { PollResults(it.numplayers, it.results.map { PollResult(it.value, it.numvotes) }) }
    )
}
