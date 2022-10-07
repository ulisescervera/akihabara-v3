package com.gmail.uli153.akihabara3.data.repositories

import com.gmail.uli153.akihabara3.data.entities.BggItem

enum class SearchTypes(val value: String) {
    Boardgame("boardgame"),
    BoardgameExpansion("boardgameexpansion"),
    BoardgameAccessory("boardgameaccessory"),
    Videgame("videogame")
}

interface BggRepository {

    suspend fun search(query: String, page: Int, pageSize: Int): List<BggItem>

    suspend fun search(query: String, types: Set<SearchTypes>): List<BggItem>
}
