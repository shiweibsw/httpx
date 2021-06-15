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
            val s = HttpManager.getManager().doTestWithResp()
            if (s is Result.Success) {
                Log.i(TAG, "success: ${s.data}")
            } else if (s is Result.Error) {
                Log.i(TAG, "error: ${s.code}--msg:${s.msg}")
            }
        }
    }
}