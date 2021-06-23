package com.hengda.frame.httpx.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hengda.frame.httpx.http.HttpManager
import com.hengda.frame.httpx.library.handle.onDefError
import com.hengda.frame.httpx.library.handle.onError
import com.hengda.frame.httpx.library.handle.onSuccess
import kotlinx.coroutines.launch

/**
 * @Author shiwei
 * @Date 2021/6/23-15:01
 * @Email shiweibsw@gmail.com
 */
class MainViewModel : ViewModel() {

    private val _respBody = MutableLiveData<String>()
    val respBody: LiveData<String> = _respBody

    private fun setRespBody(content: String) {
        _respBody.value = content
    }
    
    fun doWithBaseRequest() {
        viewModelScope.launch {
            HttpManager.getManager().doWithBaseRequest().apply {
                onSuccess { data ->
                    setRespBody(data.toString())
                }
                onDefError {
                    setRespBody("error: ${it.message}")
                }
            }
        }
    }

    fun doWithFormatResponse() {
        viewModelScope.launch {
            HttpManager.getManager().doWithFormatResponse().apply {
                onSuccess { data ->
                    setRespBody(data.toString())
                }
                onError { code, msg ->
                    setRespBody("error: ${code}--msg:${msg}")
                }
            }
        }
    }

    fun doWithExtraBaseUrl() {
        viewModelScope.launch {
            HttpManager.getManager().doWithExtraBaseUrl().apply {
                onSuccess { data ->
                    setRespBody(data.toString())
                }
                onError { code, msg ->
                    setRespBody("error: ${code}--msg:${msg}")
                }
            }
        }
    }

}