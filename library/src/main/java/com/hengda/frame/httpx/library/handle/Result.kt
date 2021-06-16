package com.hengda.frame.httpx.library.handle

import java.lang.Exception

sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val code: Int?, val msg: String?) : Result<Nothing>()
    data class DefError(val exception: Exception) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[code=$code,msg=$msg]"
            is DefError -> "DefError[exception=$exception]"
        }
    }
}

inline fun <reified T> Result<T>.onSuccess(success: (T) -> Unit) {
    if (this is Result.Success) {
        success(data)
    }
}

inline fun <reified T> Result<T>.onError(error: (Int?, String?) -> Unit) {
    if (this is Result.Error) {
        error(code, msg)
    }
}

inline fun <reified T> Result<T>.onDefError(error: (Exception) -> Unit) {
    if (this is Result.DefError) {
        error(exception)
    }
}
