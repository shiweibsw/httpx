package com.hengda.frame.httpx.http

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.hengda.frame.httpx.bean.RepeaterTimerBeanParent
import com.hengda.frame.httpx.bean.TestBean
import com.hengda.frame.httpx.bean.TestBeanChind
import com.hengda.frame.httpx.library.BaseHttpManager
import com.hengda.frame.httpx.library.handle.Result
import com.hengda.frame.httpx.library.interceptor.CommonParameterInterceptor
import com.hengda.frame.httpx.library.interceptor.ExtraBaseUrlInterceptor

class HttpManager : BaseHttpManager() {
    private val TAG = "HttpManager"

    //==============your codes ====================

    suspend fun doWithBaseRequest(): LiveData<Result<TestBean>> =
        try {
            flowRequest(apiService.test())
        } catch (e: Exception) {
            Log.i(TAG, "doWithBaseRequest: ${e.message}")
            liveData { }
        }

    suspend fun doWithFormatResponse(): LiveData<Result<TestBeanChind?>> =
        flowRequest(apiService.test1())

    suspend fun doWithExtraBaseUrl(): LiveData<Result<RepeaterTimerBeanParent?>> =
        flowRequest(apiService.test2())

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