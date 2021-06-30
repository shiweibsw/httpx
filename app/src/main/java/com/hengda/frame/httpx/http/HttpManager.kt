package com.hengda.frame.httpx.http

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.hengda.frame.httpx.bean.RepeaterTimerBeanParent
import com.hengda.frame.httpx.bean.TestBean
import com.hengda.frame.httpx.bean.TestBeanChind
import com.hengda.frame.httpx.library.BaseHttpManager
import com.hengda.frame.httpx.library.handle.Result
import com.hengda.frame.httpx.library.interceptor.CommonParameterInterceptor
import com.hengda.frame.httpx.library.interceptor.ExtraBaseUrlInterceptor
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

    suspend fun doWithExtraBaseUrl(): LiveData<Result<RepeaterTimerBeanParent?>> =
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
                setBaseUrl("http://47.93.76.140:8214/api/")
                setSuccessCode(1)
                provideOkHttpBuilder()
                    .addInterceptor(
                        CommonParameterInterceptor(
                            hashMapOf(
                                "p" to "z",
                                "cabinet_num" to "12000108"
                            )
                        )
                    ).addInterceptor(
                        ExtraBaseUrlInterceptor(
                            "publish",
                            "http://47.93.76.140:8215/api/"
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