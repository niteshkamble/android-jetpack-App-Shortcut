package com.example.shortcutexampleapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AppViewModel: ViewModel() {
    var  appShortCutData by mutableStateOf<String?>(null)
        private set
     var appShortcutType by mutableStateOf<ShortCutType?>(null)
        private set

    fun onShortCutClicked(type: ShortCutType){
        appShortcutType = type
    }

    fun onShortCutDataPresent(data:String){
        appShortCutData = data
    }
}

