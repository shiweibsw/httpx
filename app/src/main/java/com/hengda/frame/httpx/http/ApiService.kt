package com.hengda.frame.httpx.http

import com.hengda.frame.httpx.bean.TestBean
import com.hengda.frame.httpx.bean.TestBeanChind
import com.hengda.frame.httpx.bean.WeatherInfo
import com.hengda.frame.httpx.library.config.DOMAIN
import com.hengda.frame.httpx.library.response.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {

    @GET("app/mock/255859/httpx_test_api")
    suspend fun test(): TestBean

    @GET("app/mock/255859/httpx_test_api")
    suspend fun test1(): ApiResponse<TestBeanChind>

    @Headers("${DOMAIN}:weather_api")
    @GET("api/tianqi")
    suspend fun test2(
        @Query("city") city: String = "长沙",
        @Query("type") type: Int = 1
    ): ApiResponse<WeatherInfo>
}