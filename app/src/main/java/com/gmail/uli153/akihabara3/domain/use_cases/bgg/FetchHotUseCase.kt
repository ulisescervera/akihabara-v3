package com.gmail.uli153.akihabara3.domain.use_cases.bgg

import com.gmail.uli153.akihabara3.data.DataWrapper
import com.gmail.uli153.akihabara3.data.repositories.BggRepository
import com.gmail.uli153.akihabara3.domain.models.BggHotItem
import com.gmail.uli153.akihabara3.domain.toModel
import java.util.concurrent.CancellationException

class FetchHotUseCase(val repository: BggRepository) {

    suspend operator fun invoke(): DataWrapper<List<BggHotItem>> {
        return try {
            DataWrapper.Success(repository.fetchHot().map { it.toModel() })
        } catch (e: Throwable) {
            if (e is CancellationException) throw e
            DataWrapper.Error(e)
        }
    }
}
