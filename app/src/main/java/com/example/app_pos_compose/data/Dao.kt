package com.example.app_pos_compose.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {
    @Insert
    suspend fun insertMenu(menu: Menu)

    @Update
    suspend fun updateMenu(menu: Menu)

    @Delete
    suspend fun deleteMenu(menu: Menu)

    @Query("SELECT * FROM menu")
    fun getAllMenu(): Flow<List<Menu>>

    @Query("SELECT COUNT(*) FROM `menu`")
    fun countMenu() : Flow<Int>
}

@Dao
interface TableDao {
    @Insert
    suspend fun insertTable(table: Table)

    @Update
    suspend fun updateTable(table: Table)

    @Delete
    suspend fun deleteTable(table: Table)

    @Query("SELECT * FROM `table`")
    fun getAllTables() : Flow<List<Table>>

    @Query("SELECT COUNT(id) FROM `table`")
    fun countTables() : Flow<Int>

    @Query("DELETE FROM `table` WHERE tableNum > :tableNum")
    fun deleteTableById(tableNum : Int)
}

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder()

    @Query("SELECT * FROM `order` WHERE parentId =:id")
    fun getOrderByOrderTableId(id : Int) : Flow<List<Order>>
}