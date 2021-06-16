
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
