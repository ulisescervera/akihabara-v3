package com.gmail.uli153.akihabara3.domain.models

data class BggHotItem(
    val id: Int,
    val rank: Int,
    val name: String,
    val thumbnail: String?,
    val yearPublished: Int?
)
