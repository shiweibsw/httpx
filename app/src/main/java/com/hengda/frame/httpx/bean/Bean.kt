package com.hengda.frame.httpx.bean

data class TestBean(var status: Int, var msg: String, var data: TestBeanChind)

data class TestBeanChind(var code_path: String)