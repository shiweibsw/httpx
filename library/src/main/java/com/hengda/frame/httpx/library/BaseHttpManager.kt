package com.hengda.frame.httpx.library

import android.util.Log
import com.hengda.frame.httpx.library.config.DEFAULT_SUCCESS_CODE
import com.hengda.frame.httpx.library.config.DEFAULT_TIMEOUT
import com.hengda.frame.httpx.library.handle.Result
import com.hengda.frame.httpx.library.response.ApiResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

abstract class BaseHttpManager {
    private var baseUrl = ""
    private var successCode = DEFAULT_SUCCESS_CODE
    private var timeout = DEFAULT_TIMEOUT
    private val mRetrofit by lazy { createRetrofit() }
    private val okHttpClient by lazy { createOkHttpClient() }

    fun getRetrofit(): Retrofit = mRetrofit

    /**
     * set the baseurl
     * this method must be called before createOkHttpClient
     */
    fun setBaseUrl(url: String) {
        baseUrl = url
    }

    /**
     * set the basic success code ,default value is 1
     * this method must be called before createOkHttpClient
     */
    fun setSuccessCode(code: Int) {
        successCode = code
    }

    /**
     * set the default timeout include connecttimeout,writetimeout,and readtimeout
     * this method must be called before createOkHttpClient
     */
    fun setDefaultTimeout(time: Long) {
        if (time <= 0) {
            throw IllegalArgumentException("Time must be greater than 0")
        }
        timeout = time
    }

    private fun createOkHttpClient(): OkHttpClient = OkHttpClient.Builder().apply {
        connectTimeout(timeout, TimeUnit.SECONDS)
        writeTimeout(timeout, TimeUnit.SECONDS)
        readTimeout(timeout, TimeUnit.SECONDS)
        retryOnConnectionFailure(true)
        addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder().header("Accept", "application/json")
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        addInterceptor(HttpLoggingInterceptor { message ->
            Log.i(
                "HttpManager",
                message
            )
        }.also { it.level = HttpLoggingInterceptor.Level.BODY })
    }.build()

    private fun createRetrofit(): Retrofit = Retrofit.Builder().apply {
        baseUrl(baseUrl)
        client(okHttpClient)
        addConverterFactory(GsonConverterFactory.create())
        addCallAdapterFactory(CoroutineCallAdapterFactory())
    }.build()

    suspend fun <T> request(deferred: Deferred<Response<T>>): Result<T?> =
        withContext(Dispatchers.IO) {
            try {
                val response = deferred.await()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Result.Success(response.body())
                    } else {
                        Result.DefError(java.lang.Exception(response.errorBody().toString()))
                    }
                } else {
                    Result.DefError(java.lang.Exception(response.errorBody().toString()))
                }
            } catch (e: Exception) {
                Result.DefError(e)
            }
        }

    suspend fun <T> requestWithResp(deferred: Deferred<Response<ApiResponse<T>>>): Result<T?> =
        withContext(Dispatchers.IO) {
            try {
                val response = deferred.await()
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.getCode() == successCode) {
                        Result.Success(response.body()?.getDatas())
                    } else {
                        Result.Error(response.body()?.getCode(), response.body()?.getMsg())
                    }
                } else {
                    Result.Error(response.code(), response.errorBody().toString())
                }
            } catch (e: Exception) {
                Result.Error(-1, e.message.toString())
            }
        }
}