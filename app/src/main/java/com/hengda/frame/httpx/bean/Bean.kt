package com.hengda.frame.httpx.bean

/**
 * @Author shiwei
 * @Date 2021/4/8-9:31
 * @Email shiweibsw@gmail.com
 */
data class TestBean(var status: Int, var msg: String, var data: TestBeanChind)

data class TestBeanChind(var code_path: String)