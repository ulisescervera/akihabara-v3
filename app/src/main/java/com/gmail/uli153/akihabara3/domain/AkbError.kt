package com.gmail.uli153.akihabara3.domain

sealed class AkbError(message: String? = null): Throwable(message) {
    object ItemNotFound: AkbError()
}
