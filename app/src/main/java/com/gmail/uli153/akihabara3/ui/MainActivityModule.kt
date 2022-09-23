package com.gmail.uli153.akihabara3.ui

import com.gmail.uli153.akihabara3.data.repositories.AkbRepository
import com.gmail.uli153.akihabara3.domain.use_case.product.ProductsUseCases
import com.gmail.uli153.akihabara3.domain.use_case.transaction.TransactionsUseCases
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductsViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class MainActivityModule {

    @Provides
    @ViewModelScoped
    fun productsViewModel(
        productsUseCases: ProductsUseCases,
        transactionsUseCases: TransactionsUseCases
    ) = ProductsViewModel(productsUseCases, transactionsUseCases)
}
