package com.gmail.uli153.akihabara3.data.repositories

import android.util.Log
import com.gmail.uli153.akihabara3.data.entities.BggHotItemResponse
import com.gmail.uli153.akihabara3.data.entities.BggItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

class BggRepositoryImpl: BggRepository {

    val baseUrl = "https://api.geekdo.com/xmlapi2/"

    val service: BggService by lazy {
        val logger = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.HEADERS)
        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .callTimeout(30, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(SimpleXmlConverterFactory.createNonStrict(Persister(AnnotationStrategy())))
            .build()
            .create(BggService::class.java)
    }

    override suspend fun search(query: String, page: Int, pageSize: Int): List<BggItem> = withContext(Dispatchers.IO) {
        return@withContext service.search(query).items
            .let { it.subList(page, Math.min(page + pageSize, it.size)) }
            .map { it.id }
            .joinToString(",")
            .let { async { service.getItems(it) } }.await().items
    }

    override suspend fun search(query: String, types: Set<SearchTypes>): List<BggItem> = withContext(Dispatchers.IO) {
        val typesQuery = types.map { it.value }.joinToString(",")
        return@withContext service.search(query, typesQuery).items
            .map { it.id }
            .joinToString(",")
            .let { async { service.getItems(it) } }.await().items
    }

    override suspend fun fetchHot(): List<BggHotItemResponse> = withContext(Dispatchers.IO) {
        return@withContext service.getHot().items
    }

    override suspend fun getItem(id: Long): BggItem = withContext(Dispatchers.IO) {

    }
}
