package com.example.app_pos_compose

import android.app.Application
import com.example.app_pos_compose.data.AppContainer
import com.example.app_pos_compose.data.AppDataContainer

class PosApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}