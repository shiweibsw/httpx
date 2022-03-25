package com.github.frame.httpx.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.frame.httpx.http.HttpManager
import com.github.frame.httpx.library.handle.onDefError
import com.github.frame.httpx.library.handle.onError
import com.github.frame.httpx.library.handle.onSuccess
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _respBody = MutableLiveData<String>()
    val respBody: LiveData<String> = _respBody

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private fun setRespBody(content: String) {
        _respBody.postValue(content)
    }

    /**
     * Do not use GlobalScope.launch() .Otherwise, you need to deal with the lifecycle yourself.
     */
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
            HttpManager.getManager().doWithExtraBaseUrl { _isLoading.value = it }.apply {
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