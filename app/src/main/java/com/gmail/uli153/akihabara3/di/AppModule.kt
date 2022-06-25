package com.gmail.uli153.akihabara3.di

import android.content.Context
import com.gmail.uli153.akihabara3.AkihabaraDatabase
import com.gmail.uli153.akihabara3.data.repositories.AkbRepository
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
}