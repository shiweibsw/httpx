package com.hengda.frame.httpx.library.handle

sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val code: Int?, val msg: String?) : Result<Nothing>()
    data class DefError(val exception: Throwable) : Result<Nothing>()
    data class Loading(val isLoading: Boolean) : Result<Nothing>()
}

inline fun <reified T> Result<T>.onLoading(loading: (isLoading: Boolean) -> Unit) {
    if (this is Result.Loading) {
        loading(isLoading)
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

inline fun <reified T> Result<T>.onDefError(error: (Throwable) -> Unit) {
    if (this is Result.DefError) {
        error(exception)
    }
}
