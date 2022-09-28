package com.gmail.uli153.akihabara3.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.uli153.akihabara3.data.entities.ProductType
import com.gmail.uli153.akihabara3.domain.models.Product
import com.gmail.uli153.akihabara3.domain.models.Transaction
import com.gmail.uli153.akihabara3.domain.use_cases.product.ProductsUseCases
import com.gmail.uli153.akihabara3.domain.use_cases.transaction.TransactionsUseCases
import com.gmail.uli153.akihabara3.utils.DataWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsUseCases: ProductsUseCases,
    private val transactionsUseCases: TransactionsUseCases
): ViewModel() {

    val products: LiveData<DataWrapper<List<Product>>> = productsUseCases.getProductsUseCase().asLiveData()
    val driks: LiveData<DataWrapper<List<Product>>> = productsUseCases.getDrinksUseCase().asLiveData()
    val foods: LiveData<DataWrapper<List<Product>>> = productsUseCases.getFoodsUseCase().asLiveData()
    val transactions: LiveData<DataWrapper<List<Transaction>>> = transactionsUseCases.getTransactionsUseCase().asLiveData()
    val balance: LiveData<BigDecimal> = transactionsUseCases.getBalanceUseCase().asLiveData()

    fun buyProduct(product: Product, listener: ((Long) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.Main) {
            val id = productsUseCases.buyProductUseCase(product)
            listener?.invoke(id)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            productsUseCases.deleteProductUseCase(product)
        }
    }

    fun addBalance(amount: BigDecimal, title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionsUseCases.addBalanceUseCase(amount, title)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionsUseCases.deleteTransactionUseCase(id)
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            productsUseCases.toggleFavoriteUseCase(product)
        }
    }

    fun addProduct(
        id: Long,
        type: ProductType,
        name: String,
        price: BigDecimal,
        defaultImage: Int,
        customImage: File?,
        favorite: Boolean
    ) {
        val newProduct = Product(id, type, name, price, defaultImage, customImage, favorite)
        viewModelScope.launch(Dispatchers.IO) {
            productsUseCases.createProductUseCase(newProduct)
        }
    }

}
