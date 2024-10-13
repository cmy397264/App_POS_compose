package com.example.app_pos_compose.data

import kotlinx.coroutines.flow.Flow

interface RoomRepository {
    suspend fun insertMenu(menu: Menu)
    suspend fun updateMenu(menu: Menu)
    suspend fun deleteMenu(menu: Menu)
    fun getAllMenu(): Flow<List<Menu>>
}