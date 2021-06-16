package com.hengda.frame.httpx.library

import android.util.Log
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
    private val DEFAULT_TIMEOUT: Long = 10L
    private val mRetrofit by lazy { createRetrofit() }
    private val okHttpClient by lazy { createOkHttpClient() }
    private var successCode = 1

    fun getRetrofit(): Retrofit = mRetrofit

    fun setBaseUrl(url: String) {
        baseUrl = url
    }

    fun setSuccessCode(code: Int) {
        successCode = code
    }

    private fun createOkHttpClient(): OkHttpClient = OkHttpClient.Builder().apply {
        connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
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