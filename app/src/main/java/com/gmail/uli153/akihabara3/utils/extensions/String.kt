package com.gmail.uli153.akihabara3.utils.extensions

fun String.extractInt(): Int? {
    return try {
        this.replace("\\D".toRegex(), "").takeIf { it.isNotEmpty() }?.let {
            Integer.parseInt(it)
        }
    } catch (e: Exception) {
        null
    }
}