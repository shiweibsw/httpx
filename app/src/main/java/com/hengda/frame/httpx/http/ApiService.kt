package com.hengda.frame.httpx.http

import com.hengda.frame.httpx.bean.RepeaterTimerBeanParent
import com.hengda.frame.httpx.bean.TestBean
import com.hengda.frame.httpx.bean.TestBeanChind
import com.hengda.frame.httpx.library.config.DOMAIN
import com.hengda.frame.httpx.library.response.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {

    @GET("goods/get_goods_code")
    suspend fun test(): TestBean

    @GET("goods/get_goods_code")
    suspend fun test1(): ApiResponse<TestBeanChind>

    @Headers("${DOMAIN}:publish")
    @GET("cabinet/getPowerControlTime")
    suspend fun test2(): ApiResponse<RepeaterTimerBeanParent>
}