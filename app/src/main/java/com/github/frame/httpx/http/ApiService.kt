package com.github.frame.httpx.http

import com.github.frame.httpx.bean.PagingBean
import com.github.frame.httpx.bean.TestBean
import com.github.frame.httpx.bean.TestBeanChind
import com.github.frame.httpx.bean.WeatherInfo
import com.github.frame.httpx.library.config.DOMAIN
import com.github.frame.httpx.library.response.ApiResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {

    @GET("app/mock/255859/httpx_test_api")
    fun test(): Deferred<Response<TestBean>>

    @GET("app/mock/255859/httpx_test_api")
    fun test1(): Deferred<Response<ApiResponse<TestBeanChind>>>

    @Headers("${DOMAIN}:tianqi")
    @GET("api/tianqi")
    fun test2(
        @Query("city") city: String = "长沙",
        @Query("type") type: Int = 1
    ): Deferred<Response<ApiResponse<WeatherInfo>>>

    @GET("app/mock/255859/jetpack_paging")
    fun test3(): Deferred<Response<ApiResponse<List<PagingBean>>>>
}