package com.hengda.frame.httpx.viewmodels

import androidx.lifecycle.*
import com.hengda.frame.httpx.http.HttpManager
import com.hengda.frame.httpx.library.handle.onDefError
import com.hengda.frame.httpx.library.handle.onError
import com.hengda.frame.httpx.library.handle.onLoading
import com.hengda.frame.httpx.library.handle.onSuccess
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _respBody = MutableLiveData<String>()
    val respBody: LiveData<String> = _respBody

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private lateinit var viewLifecycleOwner: LifecycleOwner

    fun setLifecycleOwner(lifecycle: LifecycleOwner) {
        viewLifecycleOwner = lifecycle
    }

    private fun setRespBody(content: String) {
        _respBody.value = content
    }

    /**
     * Do not use GlobalScope.launch() .Otherwise, you need to deal with the lifecycle yourself.
     */
    fun doWithBaseRequest() {
        viewModelScope.launch {
            HttpManager.getManager().doWithBaseRequest()
                .observe(viewLifecycleOwner, Observer {
                    it.apply {
                        onLoading {
                            _isLoading.value = it
                        }
                        onSuccess { data ->
                            setRespBody(data.toString())
                        }
                        onDefError { e ->
                            setRespBody("error: ${e.message}")
                        }
                    }
                })
        }
    }

    fun doWithFormatResponse() {
        viewModelScope.launch {
            HttpManager.getManager().doWithFormatResponse().observe(viewLifecycleOwner, Observer {
                it.apply {
                    onSuccess { data ->
                        setRespBody(data.toString())
                    }
                    onError { code, msg ->
                        setRespBody("error: ${code}--msg:${msg}")
                    }
                }
            })
        }
    }

    fun doWithExtraBaseUrl() {
        viewModelScope.launch {
            HttpManager.getManager().doWithExtraBaseUrl().observe(viewLifecycleOwner, Observer {
                it.apply {
                    onSuccess { data ->
                        setRespBody(data.toString())
                    }
                    onError { code, msg ->
                        setRespBody("error: ${code}--msg:${msg}")
                    }
                }
            })
        }
    }

}