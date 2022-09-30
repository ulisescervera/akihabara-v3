package com.gmail.uli153.akihabara3.domain.use_cases.bgg

import com.gmail.uli153.akihabara3.data.repositories.BggRepository
import com.gmail.uli153.akihabara3.domain.models.BggSearchItem
import com.gmail.uli153.akihabara3.domain.toModel

class SearchBggUseCase(private val repository: BggRepository) {

    suspend operator fun invoke(query: String): List<BggSearchItem> {
        return repository.search(query).map { it.toModel()}
    }
}
