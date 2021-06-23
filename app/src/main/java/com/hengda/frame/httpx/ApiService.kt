package com.hengda.frame.httpx

import com.hengda.frame.httpx.bean.RepeaterTimerBeanParent
import com.hengda.frame.httpx.bean.TestBean
import com.hengda.frame.httpx.bean.TestBeanChind
import com.hengda.frame.httpx.library.config.DOMAIN
import com.hengda.frame.httpx.library.response.ApiResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {

    @GET("goods/get_goods_code")
    fun test(): Deferred<Response<TestBean>>

    @GET("goods/get_goods_code")
    fun test1(): Deferred<Response<ApiResponse<TestBeanChind>>>

    @Headers("${DOMAIN}:publish")
    @GET("cabinet/getPowerControlTime")
    fun test2(): Deferred<Response<ApiResponse<RepeaterTimerBeanParent>>>
}