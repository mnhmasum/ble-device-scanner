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

sealed class ServerResponseState<T> {
    class Loading<T> : ServerResponseState<T>()
    data class ReadSuccess<T>(var data: T) : ServerResponseState<T>()
    data class WriteSuccess<T>(var data: T) : ServerResponseState<T>()
    data class NotifySuccess<T>(var data: T) : ServerResponseState<T>()
    companion object {
        fun <T> readSuccess(data: T): ServerResponseState<T> =
            ReadSuccess(data)

        fun <T> writeSuccess(data: T): ServerResponseState<T> =
            WriteSuccess(data)

        fun <T> notifySuccess(data: T): ServerResponseState<T> =
            NotifySuccess(data)

        fun <T> loading(): ServerResponseState<T> =
            Loading()

    }
}
