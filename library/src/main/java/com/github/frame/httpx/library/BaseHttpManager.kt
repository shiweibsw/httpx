package com.github.frame.httpx.library

import android.util.Log
import com.github.frame.httpx.library.config.DEFAULT_LOGGER_TAG
import com.github.frame.httpx.library.config.DEFAULT_SUCCESS_CODE
import com.github.frame.httpx.library.config.DEFAULT_TIMEOUT
import com.github.frame.httpx.library.handle.Result
import com.github.frame.httpx.library.response.ApiResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

abstract class BaseHttpManager {
    private var baseUrl = ""
    private var successCode = DEFAULT_SUCCESS_CODE
    private var timeout = DEFAULT_TIMEOUT
    private var loggerTag: String = DEFAULT_LOGGER_TAG
    private val _mRetrofit by lazy { createRetrofit() }
    private val _okHttpBuilder by lazy { createOkHttpBuilder() }

    fun provideRetrofit(): Retrofit = _mRetrofit

    fun provideOkHttpBuilder(): OkHttpClient.Builder = _okHttpBuilder

    /**
     * set the baseurl
     * this method must be called before createOkHttpBuilder
     *
     * @see createOkHttpBuilder
     */
    fun setBaseUrl(url: String) {
        baseUrl = Objects.requireNonNull(url, "baseUrl == null")
    }

    /**
     * set the basic success code ,default value is 1
     * this method must be called before createOkHttpBuilder
     *
     * @see createOkHttpBuilder
     */
    fun setSuccessCode(code: Int) {
        successCode = code
    }

    /**
     * set the default timeout include connecttimeout,writetimeout,and readtimeout
     * this method must be called before createOkHttpBuilder
     *
     * @see createOkHttpBuilder
     */
    fun setDefaultTimeout(time: Long) {
        if (time <= 0) {
            throw IllegalArgumentException("Time must be greater than 0")
        }
        timeout = time
    }

    /**
     * set the http logger tag
     * log interception is enabled by default for all network requests
     */
    fun setLoggerTag(tag: String) {
        loggerTag = tag
    }

    /**
     * Just return the builder not the client.Therefore, we can add other custom interceptors.
     * Here are two interceptors that have been forced to be added.
     */
    private fun createOkHttpBuilder(): OkHttpClient.Builder = OkHttpClient.Builder().apply {
        connectTimeout(timeout, TimeUnit.SECONDS)
        writeTimeout(timeout, TimeUnit.SECONDS)
        readTimeout(timeout, TimeUnit.SECONDS)
        addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder().header("Accept", "application/json")
            val request = requestBuilder.build()
            chain.proceed(request)
        }.addInterceptor(HttpLoggingInterceptor { message ->
            Log.i(loggerTag, message)
        }.also { it.level = HttpLoggingInterceptor.Level.BODY })
    }

    private fun createRetrofit(): Retrofit = Retrofit.Builder().apply {
        baseUrl(baseUrl)
        client(_okHttpBuilder.build())
        addConverterFactory(GsonConverterFactory.create())
        addCallAdapterFactory(CoroutineCallAdapterFactory())
    }.build()

    suspend fun <T> request(
        deferred: Deferred<Response<T>>
    ): Result<T> = requestWithLoading(deferred) {}

    suspend fun <T> requestFormat(
        deferred: Deferred<Response<ApiResponse<T>>>
    ): Result<T> = requestFormatWithLoading(deferred) {}

    suspend fun <T> requestWithLoading(
        deferred: Deferred<Response<T>>,
        onLoading: (isLoading: Boolean) -> Unit
    ): Result<T> =
        try {
            onLoading(true)
            val response = deferred.await()
            if (response.isSuccessful) {
                if (response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.DefError(java.lang.Exception(response.errorBody().toString()))
                }
            } else {
                Result.DefError(java.lang.Exception(response.errorBody().toString()))
            }
        } catch (e: Exception) {
            Result.DefError(e)
        } finally {
            onLoading(false)
        }

    suspend fun <T> requestFormatWithLoading(
        deferred: Deferred<Response<ApiResponse<T>>>,
        onLoading: (isLoading: Boolean) -> Unit
    ): Result<T> =
        try {
            onLoading(true)
            val response = deferred.await()
            if (response.isSuccessful && response.body() != null) {
                if (response.body()?.getCode() == successCode) {
                    Result.Success(response.body()!!.getDatas()!!)
                } else {
                    Result.Error(response.body()?.getCode() ?: -1, response.body()?.getMsg() ?: "")
                }
            } else {
                Result.Error(response.code(), response.errorBody().toString())
            }
        } catch (e: Exception) {
            Result.Error(-1, e.message.toString())
        } finally {
            onLoading(false)
        }
}