package com.hengda.frame.httpx.library.response

class ApiResponse<T> {
    private var status: Int = 0
    private var msg: String? = null
    private var data: T? = null

    fun getCode(): Int {
        return status
    }

    fun setCode(code: Int) {
        this.status = code
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
        sb.append("status=$status msg=$msg")
        if (null != data) {
            sb.append(" data:" + data.toString())
        }
        return sb.toString()
    }
}