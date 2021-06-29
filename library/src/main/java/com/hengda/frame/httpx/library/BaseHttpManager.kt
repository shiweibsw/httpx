package com.hengda.frame.httpx.library

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.hengda.frame.httpx.library.config.DEFAULT_LOGGER_TAG
import com.hengda.frame.httpx.library.config.DEFAULT_SUCCESS_CODE
import com.hengda.frame.httpx.library.config.DEFAULT_TIMEOUT
import com.hengda.frame.httpx.library.handle.Result
import com.hengda.frame.httpx.library.handle.onSuccess
import com.hengda.frame.httpx.library.response.ApiResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

abstract class BaseHttpManager {
    private val TAG = "BaseHttpManager"
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

    suspend fun <T> flowRequest(response: T): LiveData<Result<T>> = liveData {
        flow {
            try {
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.DefError(e))
            }
        }.onStart {
            emit(Result.Loading(true))
        }.onCompletion {
            emit(Result.Loading(false))
        }.collectLatest { emit(it) }
    }

    suspend fun <T> flowRequest(response: ApiResponse<T>): LiveData<Result<T?>> = liveData {
        flow {
            try {
                if (response.getCode() == successCode) {
                    emit(Result.Success(response.getDatas()))
                } else {
                    emit(Result.Error(response.getCode(), response.getMsg()))
                }
            } catch (e: Exception) {
                emit(Result.Error(-1, response.getMsg()))
            }
        }.onStart {
            emit(Result.Loading(true))
        }.onCompletion {
            emit(Result.Loading(false))
        }.collectLatest { emit(it) }
    }

}