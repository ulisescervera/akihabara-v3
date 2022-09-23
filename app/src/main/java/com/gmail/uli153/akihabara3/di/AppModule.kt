package com.gmail.uli153.akihabara3.di

import android.content.Context
import com.gmail.uli153.akihabara3.AkihabaraDatabase
import com.gmail.uli153.akihabara3.data.repositories.AkbRepository
import com.gmail.uli153.akihabara3.domain.use_case.product.*
import com.gmail.uli153.akihabara3.domain.use_case.transaction.*
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
        return AkbRepository(db)
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
}
