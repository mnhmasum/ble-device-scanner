package com.napco.utils

sealed class DataState<T> {

    class Loading<T> : DataState<T>()
    data class Error<T>(val errorMessage: String?, val error: Throwable) : DataState<T>()
    data class Success<T>(var data: T) : DataState<T>()

    companion object {
        fun <T> loading(): DataState<T> =
            Loading()

        fun <T> error(errorMessage: String, error: Throwable): DataState<T> =
            Error(errorMessage, error)

        fun <T> success(data: T): DataState<T> =
            Success(data)

    }
}
