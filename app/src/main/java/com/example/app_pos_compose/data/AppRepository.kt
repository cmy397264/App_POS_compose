package com.example.app_pos_compose.data

import kotlinx.coroutines.flow.Flow

class MenuRepository(private val menuDao: MenuDao) : MenuRepo {
    override suspend fun insertItem(item: Menu) = menuDao.insertMenu(item)

    override suspend fun updateItem(item: Menu) = menuDao.updateMenu(item)

    override suspend fun deleteItem(item: Menu) = menuDao.deleteMenu(item)

    override fun getAllItems(): Flow<List<Menu>> = menuDao.getAllMenu()
}

class TableRepository(private val tableDao: TableDao) : TableRepo {
    override suspend fun insertItem(item: Table) = tableDao.insertTable(item)

    override suspend fun updateItem(item: Table) = tableDao.updateTable(item)

    override suspend fun deleteItem(item: Table) = tableDao.deleteTable(item)

    override fun getAllItems(): Flow<List<Table>> = tableDao.getAllTables()
    override fun countItems(): Flow<Int> = tableDao.countTables()
    override fun deleteItemByTableNum(n : Int) = tableDao.deleteTableById(n)
}