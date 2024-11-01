package com.example.app_pos_compose.data

import android.content.Context


interface AppContainer{
    val MenuRepository : MenuRepository
    val TableRepository : TableRepository
}

class AppDataContainer(private val context : Context) : AppContainer {
    override val MenuRepository : MenuRepository by lazy {
        MenuRepository(AppDatabase.getDatabase(context).menudao())
    }
    override val TableRepository : TableRepository by lazy {
        TableRepository(AppDatabase.getDatabase(context).tabledao())
    }
}