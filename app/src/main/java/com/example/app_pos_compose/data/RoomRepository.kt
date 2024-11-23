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
    fun updateFirstOrderById(tableNum: Int, firstOrder: Int)
    fun getFirstOrder(tableNum : Int) : Flow<Int>
    fun updatePriceById(tableNum: Int, price : Int)
}

interface OrderRepo {
    suspend fun insertItem(item : Order)
    suspend fun updateItem(item : Order)
    suspend fun deleteItem(item : Order)
    fun getOrderByParentId(n : Int) : Flow<List<Order>>
    fun getOrderByMenu(n : Int) : Flow<List<String>>
    fun getCountByTableId(n : Int) : Flow<Int>
    fun getLastInsertOrder() : Flow<Int>
    fun updateFirstOrder(first : Int, last : Int)
    fun deleteLastOrder(menu : String, parentId : Int)
    fun updateLastOrder(menu : String, parentId : Int)
    fun getQuantityFromLastOrder(menu : String, parentId : Int) : Flow<Int>
}
