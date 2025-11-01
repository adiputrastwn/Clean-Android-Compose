package com.adiputrastwn.cleanandroidcompose.ui.screen.leakcanary

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MemoryLeakViewModel @Inject constructor() : ViewModel() {

    fun hello(){
        println("hellow")
    }
}