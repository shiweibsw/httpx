package com.github.frame.httpx.library.handle

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val code: Int, val msg: String) : Result<Nothing>()
    data class DefError(val exception: Exception) : Result<Nothing>()
//    object Loading : Result<Nothing>()
}

suspend fun <T> Result<T>.onSuccess(success: (T) -> Unit) {
    val _this = this
    withContext(Dispatchers.Main) {
        if (_this is Result.Success) {
            success(_this.data)
        }
    }
}

suspend fun <T> Result<T>.onError(error: (Int, String) -> Unit) {
    val _this = this
    withContext(Dispatchers.Main) {
        if (_this is Result.Error) {
            error(_this.code, _this.msg)
        }
    }
}

suspend fun <T> Result<T>.onDefError(error: (Throwable) -> Unit) {
    val _this = this
    withContext(Dispatchers.Main) {
        if (_this is Result.DefError) {
            error(_this.exception)
        }
    }
}