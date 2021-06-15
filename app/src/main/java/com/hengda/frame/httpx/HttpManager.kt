package com.hengda.frame.httpx

import com.hengda.frame.httpx.bean.TestBean
import com.hengda.frame.httpx.bean.TestBeanChind
import com.hengda.frame.httpx.library.BaseHttpManager
import com.hengda.frame.httpx.library.handle.Result
import com.hengda.frame.httpx.library.response.ApiResponse
class HttpManager : BaseHttpManager() {

    //==============your code ====================

    suspend fun doTest(): Result<TestBean?> = request(apiService.test())

    suspend fun doTestWithResp(): Result<ApiResponse<TestBeanChind>?> = requestWithResp(
        apiService.test1()
    )
    //==================================

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
            var manager = HttpManager()
            manager.setSuccessCode(1)
            manager.setBaseUrl("http://47.93.76.140:8214/api/")
            manager.createApiService()
            return manager
        }
    }

    private lateinit var apiService: ApiService

    private fun createApiService() {
        apiService = getRetrofit().create(ApiService::class.java)
    }


}