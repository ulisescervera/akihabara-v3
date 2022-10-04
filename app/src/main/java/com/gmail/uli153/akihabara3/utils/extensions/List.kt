package com.gmail.uli153.akihabara3.utils.extensions

import com.gmail.uli153.akihabara3.domain.models.Product

fun List<Product>.sorted(): List<Product> {
    return this.sortedWith(compareByDescending<Product> { it.favorite }
        .thenBy { it.name.lowercase() }
        .thenBy { it.id })
}