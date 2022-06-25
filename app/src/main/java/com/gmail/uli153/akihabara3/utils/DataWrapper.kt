package com.gmail.uli153.akihabara3.utils

sealed class DataWrapper<T> {
    class Loading<T>: DataWrapper<Nothing>()
    data class Success<T>(val data: T): DataWrapper<T>()
    data class Error<T>(val error: Throwable?): DataWrapper<Nothing>()
}
