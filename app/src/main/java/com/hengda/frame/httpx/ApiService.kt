package com.hengda.frame.httpx

import com.hengda.frame.httpx.bean.TestBean
import com.hengda.frame.httpx.bean.TestBeanChind
import com.hengda.frame.httpx.library.response.ApiResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @Author shiwei
 * @Date 2021/4/8-10:00
 * @Email shiweibsw@gmail.com
 */
interface ApiService {

    @GET("goods/get_goods_code")
    fun test(
        @Query("p") p: String = "z",
        @Query("cabinet_num") cabinet_num: String = "12000108"
    ): Deferred<Response<TestBean>>

    @GET("goods/get_goods_code")
    fun test1(
        @Query("p") p: String = "z",
        @Query("cabinet_num") cabinet_num: String = "12000108"
    ): Deferred<Response<ApiResponse<TestBeanChind>>>
}