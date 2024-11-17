package com.example.app_pos_compose.data

import android.content.Context


interface AppContainer{
    val menuRepository : MenuRepository
    val tableRepository : TableRepository
    val orderRepository : OrderRepository
}

class AppDataContainer(private val context : Context) : AppContainer {
    override val menuRepository : MenuRepository by lazy {
        MenuRepository(AppDatabase.getDatabase(context).menuDao())
    }
    override val tableRepository : TableRepository by lazy {
        TableRepository(AppDatabase.getDatabase(context).tableDao())
    }
    override val orderRepository : OrderRepository by lazy {
        OrderRepository(AppDatabase.getDatabase(context).orderDao())
    }
}