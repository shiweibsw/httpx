package com.hengda.frame.httpx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.hengda.frame.httpx.library.handle.Result
import com.hengda.frame.httpx.library.handle.onError
import com.hengda.frame.httpx.library.handle.onSuccess
import kotlinx.coroutines.*
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var httpScope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btnTest).setOnClickListener {
            doTest()
        }
    }

    private fun doTest() {
        httpScope.launch {
            HttpManager.getManager().doTestWithResp().apply {
                onSuccess { data ->
                    Log.i(TAG, "success: $data")
                }
                onError { code, msg ->
                    Log.i(TAG, "error: ${code}--msg:${msg}")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        httpScope.cancel()
    }

}