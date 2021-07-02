package com.hengda.frame.httpx.http

import com.hengda.frame.httpx.bean.TestBean
import com.hengda.frame.httpx.bean.TestBeanChind
import com.hengda.frame.httpx.bean.WeatherInfo
import com.hengda.frame.httpx.library.BaseHttpManager
import com.hengda.frame.httpx.library.handle.Result
import com.hengda.frame.httpx.library.interceptor.CommonParameterInterceptor
import com.hengda.frame.httpx.library.interceptor.ExtraBaseUrlInterceptor

class HttpManager : BaseHttpManager() {

    //==============your codes ====================

    suspend fun doWithBaseRequest(): Result<TestBean?> = request(apiService.test())

    suspend fun doWithFormatResponse(): Result<TestBeanChind?> = requestFormat(apiService.test1())

    suspend fun doWithExtraBaseUrl(onLoading: (isLoading: Boolean) -> Unit): Result<WeatherInfo?> =
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
                setBaseUrl("http://rap2api.taobao.org/")
                setSuccessCode(200)
                provideOkHttpBuilder()
                    .addInterceptor(
                        CommonParameterInterceptor(
                            hashMapOf(
                                "platform" to "android"
                            )
                        )
                    )
                    .addInterceptor(
                        ExtraBaseUrlInterceptor(
                            "tianqi",
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