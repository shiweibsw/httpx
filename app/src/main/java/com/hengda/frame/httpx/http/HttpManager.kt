package com.hengda.frame.httpx.http

import com.hengda.frame.httpx.bean.RepeaterTimerBeanParent
import com.hengda.frame.httpx.bean.TestBean
import com.hengda.frame.httpx.bean.TestBeanChind
import com.hengda.frame.httpx.library.BaseHttpManager
import com.hengda.frame.httpx.library.handle.Result
import com.hengda.frame.httpx.library.interceptor.CommonParameterInterceptor
import com.hengda.frame.httpx.library.interceptor.ExtraBaseUrlInterceptor

class HttpManager : BaseHttpManager() {

    //==============your codes ====================

    suspend fun doWithBaseRequest(): Result<TestBean?> = request(apiService.test())

    suspend fun doWithFormatResponse(): Result<TestBeanChind?> = requestFormat(apiService.test1())

    suspend fun doWithExtraBaseUrl(onLoading: (isLoading: Boolean) -> Unit): Result<RepeaterTimerBeanParent?> =
        requestFormatWithLoading(apiService.test2(), onLoading)

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