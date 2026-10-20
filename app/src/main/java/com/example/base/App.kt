package com.example.base

import android.app.Application
import com.example.base.manager.storage.StorageManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        StorageManager.init(this)
    }
}
