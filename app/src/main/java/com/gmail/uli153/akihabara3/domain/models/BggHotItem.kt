package com.gmail.uli153.akihabara3.domain.models

data class BggHotItem(
    val id: Long,
    val rank: Int,
    val name: String,
    val thumbnail: String?,
    val yearPublished: Int?
)
