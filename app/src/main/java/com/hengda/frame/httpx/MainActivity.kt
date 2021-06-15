package com.hengda.frame.httpx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.hengda.frame.httpx.library.handle.Result
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btnTest).setOnClickListener {
            doTest()
        }
    }

    private fun doTest() {
        GlobalScope.launch {
            HttpManager.getManager().doTestWithResp().apply {
                if (this is Result.Success) {
                    Log.i(TAG, "success: ${this.data}")
                } else if (this is Result.Error) {
                    Log.i(TAG, "error: ${this.code}--msg:${this.msg}")
                }
            }
        }
    }
}