package com.hengda.frame.httpx.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hengda.frame.httpx.http.HttpManager
import com.hengda.frame.httpx.library.handle.onError
import com.hengda.frame.httpx.library.handle.onSuccess
import kotlinx.coroutines.launch

/**
 * @Author shiwei
 * @Date 2021/6/23-15:01
 * @Email shiweibsw@gmail.com
 */
class MainViewModel : ViewModel() {
    private val TAG = "MainActivity"

    fun doWithBaseRequest() {
        viewModelScope.launch {
            HttpManager.getManager().doWithBaseRequest().apply {
                onSuccess { data ->
                    Log.i(TAG, "success: $data")
                }
                onError { code, msg ->
                    Log.i(TAG, "error: ${code}--msg:${msg}")
                }
            }
        }
    }

    fun doWithFormatResponse() {
        viewModelScope.launch {
            HttpManager.getManager().doWithFormatResponse().apply {
                onSuccess { data ->
                    Log.i(TAG, "success: $data")
                }
                onError { code, msg ->
                    Log.i(TAG, "error: ${code}--msg:${msg}")
                }
            }
        }
    }

    fun doWithExtraBaseUrl() {
        viewModelScope.launch {
            HttpManager.getManager().doWithExtraBaseUrl().apply {
                onSuccess { data ->
                    Log.i(TAG, "success: $data")
                }
                onError { code, msg ->
                    Log.i(TAG, "error: ${code}--msg:${msg}")
                }
            }
        }
    }

}