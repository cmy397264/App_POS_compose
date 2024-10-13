package com.example.app_pos_compose.data

import android.content.Context


interface AppContainer{
    val roomRepository : RoomRepository
}
class AppDataContainer(private val context : Context) : AppContainer {
    override val roomRepository : RoomRepository by lazy {
        OfflineMenuRepository(MenuDatabase.getDatabase(context).menudao())
    }
}