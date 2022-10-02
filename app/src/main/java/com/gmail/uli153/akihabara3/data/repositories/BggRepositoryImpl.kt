package com.gmail.uli153.akihabara3.data.repositories

import android.util.Log
import com.gmail.uli153.akihabara3.data.entities.BggItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class BggRepositoryImpl: BggRepository {

    val baseUrl = "https://api.geekdo.com/xmlapi2/"

    val service: BggService by lazy {
        val logger = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.HEADERS)
        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
            .create(BggService::class.java)
    }

    override suspend fun search(query: String, page: Int): List<BggItem> = withContext(Dispatchers.IO) {
        val startIndex = 10 * page
        return@withContext service.search(query).items
            .let { it.subList(startIndex, Math.min(startIndex + 10, it.size)) }
            .map { async { service.getItem(it.id).items } }.awaitAll().flatten()
    }
}
