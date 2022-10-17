package com.gmail.uli153.akihabara3.data.repositories

import com.gmail.uli153.akihabara3.data.entities.BggHotResponse
import com.gmail.uli153.akihabara3.data.entities.BggItemResponse
import com.gmail.uli153.akihabara3.data.entities.BggSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BggService {

    @GET("search")
    suspend fun search(
        @Query("query") query: String,
        @Query("type") types: String? = null
    ): BggSearchResponse

    @GET("thing")
    suspend fun getItem(@Query("id") id: Long, @Query("type") type: String? = null): BggItemResponse

    @GET("thing")
    suspend fun getItems(
        @Query("id", encoded = true) ids: String,
        @Query("stats") stats: Boolean = true,
        @Query("type") type: String? = null
    ): BggItemResponse

    @GET("hot")
    suspend fun getHot(): BggHotResponse
}
