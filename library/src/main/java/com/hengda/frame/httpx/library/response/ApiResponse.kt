package com.hengda.frame.httpx.library.response

class ApiResponse<T> {
    private var code: Int = 0
    private var msg: String? = null
    private var data: T? = null

    fun getCode(): Int {
        return code
    }

    fun setCode(code: Int) {
        this.code = code
    }

    fun getMsg(): String {
        return msg ?: ""
    }

    fun setMsg(msg: String) {
        this.msg = msg
    }

    fun getDatas(): T? {
        return data
    }

    fun setDatas(datas: T) {
        this.data = datas
    }

    override fun toString(): String {
        val sb = StringBuffer()
        sb.append("code=$code msg=$msg")
        if (null != data) {
            sb.append(" data:" + data.toString())
        }
        return sb.toString()
    }
}