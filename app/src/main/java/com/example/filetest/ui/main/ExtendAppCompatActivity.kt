package com.example.filetest.ui.main

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class ExtendAppCompatActivity:AppCompatActivity() {
    fun <T>  Flow<T>.launchLifeScopeCollect (doCollect: suspend (T) -> Unit){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                collect {
                    doCollect(it)
                }
            }
        }
    }

    fun <T> Flow<T>.launchLifeScopeCollectLatest (doCollect: suspend (T) -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                collectLatest {
                    doCollect(it)
                }
            }
        }
    }
}