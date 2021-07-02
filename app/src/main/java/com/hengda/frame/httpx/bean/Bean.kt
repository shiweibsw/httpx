package com.hengda.frame.httpx.bean

data class TestBean(var status: Int, var msg: String, var data: TestBeanChind)

data class TestBeanChind(var result: String)

data class WeatherInfo(
    val cityname: String,
    val temp: String,
    val WD: String,
    val WS: String,
    val wse: String,
    val SD: String,
    val weather: String,
    val pm25: String,
    val time: String
)