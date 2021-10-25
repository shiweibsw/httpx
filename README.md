
#### 
master分支为普通协程版本, flow 分支为flow版本。


#### 前言
说是框架其实就是使用协程+retrofit进行的简单封装，不得不说使用支持协程的Retrofit发起一个网络请求真的是简单的不能在简单了。

#### 目录结构


![企业微信截图_20210705100933.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1a8d2665244841df830e67037599c9e7~tplv-k3u1fbpfcp-watermark.image)

#### 使用方式

```
 implementation 'io.github.shiwebsw:httpx:1.0.0'
 
```

##### 普通协程版

定义一个ApiService， 里边定义好接口就像这样

```
interface ApiService {

    @GET("app/mock/255859/httpx_test_api")
    fun test(): Deferred<Response<TestBean>>

    @GET("app/mock/255859/httpx_test_api")
    fun test1(): Deferred<Response<ApiResponse<TestBeanChind>>>

}

```
注意返回值是一个Deferred<Response<T>>。

其次，定义一个名为HttpManager的类，此类继承自BaseHttpManager,并将下面的模板代码copy过来，然后再这个类中声明具体的网络接口，完整的一个HttpManager就像这样


```
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
                createApiService()
            }
        }
    }

    private fun createApiService() {
        apiService = provideRetrofit().create(ApiService::class.java)
    }

}

```
然后就是发起网络请求。

注意：以下方法均需运行在协程中。

```
HttpManager.getManager().doWithBaseRequest().apply {
      onSuccess { data ->}
      onDefError {exception-> }
}
```
如果你的项目后台接口返回的json均为固定格式

```
{
    "code": "200",
    "msg": "",
    "data": {
        "result": " I'm response body"
    }
}
```
那么你可以这样写

```
HttpManager.getManager().doWithFormatResponse().apply {
     onSuccess { data ->}
     onError { code, msg ->}
}
```
最大的区别在onError里，这里包含了所有错误状态，包括网络，超时以及接口自定义的一些错误状态都再这里处理即可。

没有加载状态监听？安排

```
HttpManager.getManager().doWithExtraBaseUrl { _isLoading.value = it }.apply {
    onSuccess { data ->}
    onError { code, msg ->}
}
```
注意 doWithExtraBaseUrl 这个函数中的表达式，里边的it为一个布尔值，在这里你可以根据需要自定义加载状态即可。


普通协程版就到这里吧，下边再来看下Flow版本。

<!--
##### Flow版

flow版本的Apiservice接口函数返回的是一个suspend类型，同时返回类型直接就是实体类型，注意和普通协程版的区别。

```
interface ApiService {

    @GET("app/mock/255859/httpx_test_api")
    suspend fun test(): TestBean

    @GET("app/mock/255859/httpx_test_api")
    suspend fun test1(): ApiResponse<TestBeanChind>
}

```

Flow版本的HttpManger 模板代码和普通版一样，自定义的接口代码有些区别，你需要这样写


```
 //==============your codes ====================

    suspend fun doWithBaseRequest(): LiveData<Result<TestBean>> =
        try {
            flowRequest(apiService.test())
        } catch (e: Throwable) {
            flow { emit(Result.DefError(e)) }.asLiveData()
        }
        
//===============Template codes=================
...
```
这里的try catch 主要是为了处理网络等异常，目前还没找到封装进框架里的办法，就先这样写吧😄。

最后是发起网络请求，其实就是监听livedata啦。


```
HttpManager.getManager().doWithExtraBaseUrl().observe(viewLifecycleOwner, Observer{
    it.apply {
        onLoading { _isLoading.value = it}
        onSuccess { data ->}
        onError { code, msg ->}
     }
})

```
-->

#### 模板代码的配置

注意在模板代码中你可能要修改的地方为 buildManager函数体

1. setBaseUrl（） 略过，必须配置

2.  setSuccessCode（）可选，当返回的json为固定模板时可配置此项，值为与后台约定好的数据请求成功的code值，这里的200只是个示例。

3.provideOkHttpBuilder() 可选，返回Okhttp的构造器，在这里你可以添加各种拦截器。

在框架中已经为大家实现了几个项目开发中使用频率最大的拦截器。

（1）日志拦截器

此拦截器框架默认实现不需要再通过provideOkHttpBuilder()添加，你可以通过setLoggerTag（）自定义日志的tag
```
.addInterceptor(HttpLoggingInterceptor { message ->
            Log.i(loggerTag, message)
}

```
（2）公共请求参数拦截器

实际开发中接口请求可能会有一些公共的参数，这些参数没必要声明再各个接口中，通过一个公共请求拦截器添加即可，例如以下拦截器添加一个平台信息的请求参数。

```
provideOkHttpBuilder().addInterceptor(
    CommonParameterInterceptor(
        hashMapOf(
            "platform" to "android"
        )
    )
)

```
（2）多基地址拦截器

实际项目中基地址可能不止一个，通过添加多基地址拦截器可以实现多个接口地址的请求，就像这样：


```
provideOkHttpBuilder().addInterceptor(
    ExtraBaseUrlInterceptor(
        "weather_api",
        "https://api.muxiaoguo.cn/"
    )
)

```
同时ApiService接口中对应的函数中需要增加一个header，就像这样


```
    @Headers("${DOMAIN}:weather_api")
    @GET("api/tianqi")
    suspend fun test2(
        @Query("city") city: String = "长沙",
        @Query("type") type: Int = 1
    ): ApiResponse<WeatherInfo>


```
这样就实现了多个基地址请求的配置。

一个配置了拦截器的buildManager（）函数可能长这样：

```
        private fun buildManager(): HttpManager {
            return HttpManager().apply {
                setBaseUrl("http://rap2api.taobao.org/")
                setSuccessCode((200))
                provideOkHttpBuilder()
                    .addInterceptor(
                        CommonParameterInterceptor(
                            hashMapOf(
                                "platform" to "android"
                            )
                        )
                    ).addInterceptor(
                        ExtraBaseUrlInterceptor(
                            "weather_api",
                            "https://api.muxiaoguo.cn/"
                        )
                    )
                createApiService()
            }
        }

```


以上。





