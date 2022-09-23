package com.gmail.uli153.akihabara3.domain.use_case.product

import androidx.lifecycle.viewModelScope
import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.data.repositories.AkbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToggleFavoriteUseCase(private val repository: AkbRepository) {

    suspend operator fun invoke(product: Product) {
        val edited = product.copy(favorite = !product.favorite)
        repository.addProduct(edited)
    }
}
