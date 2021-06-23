package com.hengda.frame.httpx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hengda.frame.httpx.databinding.ActivityMainBinding
import com.hengda.frame.httpx.http.HttpManager
import com.hengda.frame.httpx.library.handle.onError
import com.hengda.frame.httpx.library.handle.onSuccess
import com.hengda.frame.httpx.viewmodels.MainViewModel
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
            .apply {
                viewmodel = viewModel
                lifecycleOwner = this@MainActivity
            }
    }
}