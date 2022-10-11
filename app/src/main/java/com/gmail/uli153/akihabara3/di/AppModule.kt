package com.gmail.uli153.akihabara3.di

import android.content.Context
import com.gmail.uli153.akihabara3.data.AkihabaraDatabase
import com.gmail.uli153.akihabara3.data.repositories.AkbRepository
import com.gmail.uli153.akihabara3.data.repositories.AkbRepositoryImpl
import com.gmail.uli153.akihabara3.data.repositories.BggRepository
import com.gmail.uli153.akihabara3.data.repositories.BggRepositoryImpl
import com.gmail.uli153.akihabara3.domain.use_cases.bgg.FetchHotUseCase
import com.gmail.uli153.akihabara3.domain.use_cases.bgg.SearchBggUseCase
import com.gmail.uli153.akihabara3.domain.use_cases.product.*
import com.gmail.uli153.akihabara3.domain.use_cases.transaction.*
import com.gmail.uli153.akihabara3.utils.PreferenceUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun databaseProvider(@ApplicationContext context: Context): AkihabaraDatabase {
        return AkihabaraDatabase.buildDatabase(context)
    }

    @Provides
    @Singleton
    fun akbRepository(db: AkihabaraDatabase): AkbRepository {
        return AkbRepositoryImpl(db)
    }

    @Provides
    @Singleton
    fun bggRepository(): BggRepository {
        return BggRepositoryImpl()
    }

    @Provides
    @Singleton
    fun preferenceUtils(@ApplicationContext context: Context): PreferenceUtils {
        return PreferenceUtils(context)
    }

    @Provides
    @Singleton
    fun productsUseCases(repository: AkbRepository): ProductsUseCases {
        return ProductsUseCases(
            GetProductsUseCase(repository),
            GetDrinksUseCase(repository),
            GetFoodsUseCase(repository),
            BuyProductUseCase(repository),
            CreateProductUseCase(repository),
            DeleteProductUseCase(repository),
            ToggleFavoriteUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun transactionsUseCases(repository: AkbRepository): TransactionsUseCases {
        return TransactionsUseCases(
            GetBalanceUseCase(repository),
            GetTransactionsUseCase(repository),
            AddBalanceUseCase(repository),
            DeleteTransactionUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun searchBggUseCase(repository: BggRepository): SearchBggUseCase {
        return SearchBggUseCase(repository)
    }

    @Provides
    @Singleton
    fun fetchHotBggUseCase(repository: BggRepository): FetchHotUseCase {
        return FetchHotUseCase(repository)
    }

}
