package com.github.frame.httpx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.github.frame.httpx.databinding.ActivityMainBinding
import com.github.frame.httpx.viewmodels.MainViewModel
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
            .apply {
                viewmodel = viewModel
                lifecycleOwner = this@MainActivity
            }
    }
}