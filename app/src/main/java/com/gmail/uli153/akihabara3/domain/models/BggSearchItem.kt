package com.gmail.uli153.akihabara3.domain.models

data class BggSearchItem(
    val id: Long,
    val thumbnail: String?,
    val image: String?,
    val names: List<Name>,
    val description: String,
    val yearPublished: Int?,
    val minPlayers: Int?,
    val maxPlayers: Int?,
    val playingTime: Int?,
    val minAge: Int?,
    val ranks: List<Rank>?,
    val rating: Float?,
    val geekRating: Float?,
    val weight: Float?,
    val votes: Int?,
    val categories: List<BoardgameLink>,
    val mechanics: List<BoardgameLink>,
    val polls: List<Poll>
) {
    val name: String? = names.firstOrNull { it.type == NameType.PRIMARY }?.value ?: names.firstOrNull()?.value

    val nameAndYear: String get() {
        return name?.let {
            if (yearPublished != null) {
                "$it ($yearPublished)"
            } else {
                it
            }
        } ?: ""
    }
}

data class Name(
    val type: NameType,
    val value: String
)

data class Rank(
    val id: Int,
    val name: String,
    val position: Int,
)

data class BoardgameLink(
    val id: Int,
    val name: String
)

data class Poll(
    val type: PollType,
    val results: List<PollResults>
)

data class PollResults(
    val players: String?,
    val result: List<PollResult>
)

data class PollResult(
    val value: String,
    val votes: Int
)

enum class PollType(val pollName: String) {
    SUGGESTED_PLAYERAGE     ("suggested_playerage"),
    LANGUAGE_DEPENDENCE     ("language_dependence"),
    SUGGESTED_NUMPLAYERS    ("suggested_numplayers")
}

enum class NameType {
    PRIMARY, ALTERNATE
}
