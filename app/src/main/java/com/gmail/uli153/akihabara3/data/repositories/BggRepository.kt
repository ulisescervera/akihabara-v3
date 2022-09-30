package com.gmail.uli153.akihabara3.data.repositories

import com.gmail.uli153.akihabara3.data.entities.BggItem

interface BggRepository {

    suspend fun search(query: String): List<BggItem>
}
