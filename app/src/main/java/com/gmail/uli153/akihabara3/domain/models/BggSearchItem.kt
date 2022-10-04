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
) {
    val name: String? = names.firstOrNull { it.type == NameType.PRIMARY }?.value ?: names.firstOrNull()?.value
}

data class Name(
    val type: NameType,
    val value: String
)

enum class NameType {
    PRIMARY, ALTERNATE
}
