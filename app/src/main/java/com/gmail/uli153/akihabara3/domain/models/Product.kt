package com.gmail.uli153.akihabara3.domain.models

import com.gmail.uli153.akihabara3.data.entities.ProductType
import java.io.File
import java.math.BigDecimal

data class Product(
    val id: Long = 0,
    val type: ProductType,
    override val name: String,
    val price: BigDecimal,
    val defaultImage: Int,
    val customImage: File?,
    val favorite: Boolean = false
): Nameable
