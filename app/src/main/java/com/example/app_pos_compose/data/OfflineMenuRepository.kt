package com.example.app_pos_compose.data

import kotlinx.coroutines.flow.Flow

class OfflineMenuRepository(private val menuDao: MenuDao) : RoomRepository {
    override suspend fun insertMenu(menu: Menu) = menuDao.insertMenu(menu)

    override suspend fun updateMenu(menu: Menu) = menuDao.updateMenu(menu)

    override suspend fun deleteMenu(menu: Menu) = menuDao.deleteMenu(menu)

    override fun getAllMenu(): Flow<List<Menu>> = menuDao.getAllMenu()
}