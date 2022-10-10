package com.gmail.uli153.akihabara3.data

sealed class DataWrapper<out T> {
    object Loading : DataWrapper<Nothing>()
    data class Success<T>(val data: T): DataWrapper<T>()
    data class Error<T>(val error: Throwable?): DataWrapper<T>()
}
