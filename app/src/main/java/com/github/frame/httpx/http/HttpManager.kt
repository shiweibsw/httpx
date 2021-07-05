package com.github.frame.httpx.http

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.github.frame.httpx.bean.TestBean
import com.github.frame.httpx.bean.TestBeanChind
import com.github.frame.httpx.bean.WeatherInfo
import com.github.frame.httpx.library.BaseHttpManager
import com.github.frame.httpx.library.handle.Result
import com.github.frame.httpx.library.interceptor.CommonParameterInterceptor
import com.github.frame.httpx.library.interceptor.ExtraBaseUrlInterceptor
import kotlinx.coroutines.flow.flow

class HttpManager : BaseHttpManager() {

    //==============your codes ====================

    suspend fun doWithBaseRequest(): LiveData<Result<TestBean>> =
        try {
            flowRequest(apiService.test())
        } catch (e: Throwable) {
            flow { emit(Result.DefError(e)) }.asLiveData()
        }

    suspend fun doWithFormatResponse(): LiveData<Result<TestBeanChind?>> =
        try {
            flowRequest(apiService.test1())
        } catch (e: Throwable) {
            flow { emit(Result.Error(-1, e.message)) }.asLiveData()
        }

    suspend fun doWithExtraBaseUrl(): LiveData<Result<WeatherInfo?>> =
        try {
            flowRequest(apiService.test2())
        } catch (e: Throwable) {
            flow { emit(Result.Error(-1, e.message)) }.asLiveData()
        }

    //===============Template codes=================


    private lateinit var apiService: ApiService

    companion object {
        @Volatile
        private var httpManager: HttpManager? = null
        fun getManager(): HttpManager {
            return httpManager ?: synchronized(this) {
                httpManager ?: buildManager().also {
                    httpManager = it
                }
            }
        }

        private fun buildManager(): HttpManager {
            return HttpManager().apply {
                setBaseUrl("http://rap2api.taobao.org/")
                setSuccessCode((200))
                provideOkHttpBuilder()
                    .addInterceptor(
                        CommonParameterInterceptor(
                            hashMapOf(
                                "platform" to "android"
                            )
                        )
                    ).addInterceptor(
                        ExtraBaseUrlInterceptor(
                            "weather_api",
                            "https://api.muxiaoguo.cn/"
                        )
                    )
                createApiService()
            }
        }
    }

    private fun createApiService() {
        apiService = provideRetrofit().create(ApiService::class.java)
    }


}