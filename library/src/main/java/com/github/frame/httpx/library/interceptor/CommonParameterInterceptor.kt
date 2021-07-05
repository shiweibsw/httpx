package com.github.frame.httpx.library.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @Author shiwei
 * @Date 2021/6/23-10:35
 * @Email shiweibsw@gmail.com
 */
class CommonParameterInterceptor(var paras: Map<String, String>) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val httpUrl = request.url().newBuilder()
        paras.entries.forEach {
            httpUrl.addQueryParameter(it.key, it.value)
        }
        request = request.newBuilder().url(httpUrl.build()).build()
        return chain.proceed(request)
    }
}