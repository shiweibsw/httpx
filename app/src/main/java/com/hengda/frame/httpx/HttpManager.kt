package com.hengda.frame.httpx

import com.hengda.frame.httpx.bean.TestBean
import com.hengda.frame.httpx.bean.TestBeanChind
import com.hengda.frame.httpx.library.BaseHttpManager
import com.hengda.frame.httpx.library.handle.Result
import com.hengda.frame.httpx.library.interceptor.CommonParameterInterceptor

class HttpManager : BaseHttpManager() {

    //==============your code ====================

    suspend fun doTest(): Result<TestBean?> = request(apiService.test())

    suspend fun doTestWithResp(): Result<TestBeanChind?> = requestWithResp(apiService.test1())


    //===============Template code=============================
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
                setSuccessCode(1)
                setBaseUrl("http://47.93.76.140:8214/api/")
                /**common parameters**/
                provideOkHttpBuilder().addInterceptor(
                    CommonParameterInterceptor(
                        hashMapOf(
                            "p" to "z",
                            "cabinet_num" to "12000108"
                        )
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