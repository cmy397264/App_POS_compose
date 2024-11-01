package com.example.app_pos_compose.data

import kotlinx.coroutines.flow.Flow

interface MenuRepo {
    suspend fun insertItem(item : Menu)
    suspend fun updateItem(item : Menu)
    suspend fun deleteItem(item : Menu)
    fun getAllItems(): Flow<List<Menu>>
}

interface TableRepo {
    suspend fun insertItem(item : Table)
    suspend fun updateItem(item : Table)
    suspend fun deleteItem(item : Table)
    fun getAllItems(): Flow<List<Table>>
    fun countItems() : Flow<Int>
    fun deleteItemByTableNum(n: Int)
}
