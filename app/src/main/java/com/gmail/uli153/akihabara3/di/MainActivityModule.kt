package com.gmail.uli153.akihabara3.di

import com.gmail.uli153.akihabara3.domain.use_cases.bgg.FetchHotUseCase
import com.gmail.uli153.akihabara3.domain.use_cases.bgg.SearchBggUseCase
import com.gmail.uli153.akihabara3.domain.use_cases.product.ProductsUseCases
import com.gmail.uli153.akihabara3.domain.use_cases.transaction.TransactionsUseCases
import com.gmail.uli153.akihabara3.ui.viewmodels.BggViewModel
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductsViewModel
import com.gmail.uli153.akihabara3.utils.PreferenceUtils
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

    @Provides
    @ViewModelScoped
    fun bggViewModel(
        bggUseCase: SearchBggUseCase,
        fetchHotUseCase: FetchHotUseCase,
        preferenceUtils: PreferenceUtils
    ) = BggViewModel(fetchHotUseCase, bggUseCase,  preferenceUtils)

}
