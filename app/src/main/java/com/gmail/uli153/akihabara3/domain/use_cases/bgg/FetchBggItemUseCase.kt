package com.gmail.uli153.akihabara3.domain.use_cases.bgg

import com.gmail.uli153.akihabara3.data.DataWrapper
import com.gmail.uli153.akihabara3.data.repositories.BggRepository
import com.gmail.uli153.akihabara3.domain.AkbError
import com.gmail.uli153.akihabara3.domain.models.BggSearchItem
import com.gmail.uli153.akihabara3.domain.toModel

class FetchBggItemUseCase(val repository: BggRepository) {

    suspend operator fun invoke(id: Long): DataWrapper<BggSearchItem> {
        return try {
            val res = repository.getItem(id)?.toModel()
            if (res != null) {
                DataWrapper.Success(res)
            } else {
                DataWrapper.Error(AkbError.ItemNotFound)
            }
        } catch (e: Throwable) {
            DataWrapper.Error(e)
        }
    }
}
