package com.gmail.uli153.akihabara3.data.repositories

import android.util.Log
import com.gmail.uli153.akihabara3.data.entities.BggItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class BggRepositoryImpl: BggRepository {

    val baseUrl = "https://api.geekdo.com/xmlapi2/"

    val service: BggService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()
        .create(BggService::class.java)

    override suspend fun search(query: String): List<BggItem> = withContext(Dispatchers.IO) {
        return@withContext try {
            service.search(query).items.map { async { service.getItem(it.id).items } }.awaitAll().flatten()
        } catch (exception: Exception) {
            Log.e("AKB", exception.cause?.toString() ?: exception.message ?: exception.toString())
            emptyList()
        }
    }
}
