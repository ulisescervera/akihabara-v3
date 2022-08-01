package com.gmail.uli153.akihabara3.ui.viewmodels

import androidx.lifecycle.*
import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.data.models.ProductType
import com.gmail.uli153.akihabara3.data.models.Transaction
import com.gmail.uli153.akihabara3.data.models.TransactionType
import com.gmail.uli153.akihabara3.data.repositories.AkbRepository
import com.gmail.uli153.akihabara3.utils.DataWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(private val repo: AkbRepository): ViewModel() {

    val products: LiveData<DataWrapper<List<Product>>> = repo.fetchProducts()
        .onStart { DataWrapper.Loading<List<Product>>() }
        .map {
            val sorted = it.sortedWith(compareByDescending<Product> { it.favorite }.thenBy { it.name }.thenBy { it.id })
            DataWrapper.Success(sorted)
        }
        .asLiveData()

    val driks: LiveData<DataWrapper<List<Product>>> = products.map {
        return@map if (it is DataWrapper.Success<List<Product>>) {
            DataWrapper.Success(it.data.filter { it.type ==  ProductType.DRINK })
        } else {
            it
        }
    }

    val foods: LiveData<DataWrapper<List<Product>>> = products.map {
        return@map if (it is DataWrapper.Success<List<Product>>) {
            DataWrapper.Success(it.data.filter { it.type ==  ProductType.FOOD })
        } else {
            it
        }
    }

    val transactions: LiveData<DataWrapper<List<Transaction>>> = repo.fetchTransactions()
        .onStart { DataWrapper.Loading<List<Transaction>>() }
        .map { DataWrapper.Success(it) }
        .asLiveData()

    val balance: LiveData<BigDecimal> = transactions.map {
        return@map if (it is DataWrapper.Success<List<Transaction>>) {
            it.data.sumOf { if (it.type == TransactionType.BUY) it.amount.negate() else it.amount }
        } else {
            BigDecimal(0)
        }
    }

    fun buyProduct(product: Product, listener: ((Long) -> Unit)? = null) {
        val transaction = Transaction(
            type = TransactionType.BUY,
            date = Date(),
            title = product.name,
            amount = product.price,
            customImage = product.customImage,
            defaultImage = product.defaultImage
        )
        viewModelScope.launch(Dispatchers.IO) {
            val id = repo.addTransaction(transaction)
            if (listener != null) {
                withContext(Dispatchers.Main) {
                    listener(id)
                }
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteProduct(product)
        }
    }

    fun addBalance(amount: BigDecimal, title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val transaction = Transaction(
                type = TransactionType.BALANCE,
                date = Date(),
                title = title,
                amount = amount,
                defaultImage = 0,
                customImage = null
            )
            repo.addTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteTransaction(transaction)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteTransaction(id)
        }
    }

    fun toggleFavorite(product: Product) {
        val edited = product.copy(favorite = !product.favorite)
        viewModelScope.launch(Dispatchers.IO) {
            repo.addProduct(edited)
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addProduct(product)
        }
    }

//    val driks: LiveData<DataWrapper<List<Product>>> = repo.fetchDriks()
//        .onStart { DataWrapper.Loading<List<Product>>() }
//        .map { DataWrapper.Success(it) }
//        .asLiveData()

//    val foods: LiveData<DataWrapper<List<Product>>> = repo.fetchFoods()
//        .onStart { DataWrapper.Loading<List<Product>>() }
//        .map { DataWrapper.Success(it) }
//        .asLiveData()



}