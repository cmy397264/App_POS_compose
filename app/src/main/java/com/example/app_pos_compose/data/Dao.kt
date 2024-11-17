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

    @Query("SELECT firstOrder FROM `table` WHERE tableNum = :tableNum")
    fun getFirstOrder(tableNum : Int) : Flow<Int>

    @Query("SELECT COUNT(id) FROM `table`")
    fun countTables() : Flow<Int>

    @Query("DELETE FROM `table` WHERE tableNum > :tableNum")
    fun deleteTableById(tableNum : Int)

    @Query("UPDATE `table` SET firstOrder = :firstOrder WHERE tableNum = :tableNum")
    fun updateFirstOrderById(tableNum: Int, firstOrder : Int)

    @Query("UPDATE 'table' SET price = price + :price WHERE tableNum = :tableNum")
    fun updatePriceById(tableNum: Int, price : Int)

}

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

    @Query("SELECT * FROM `order` WHERE parentId =:id")
    fun getOrderByOrderTableId(id : Int) : Flow<List<Order>>

    @Query("SELECT COUNT(*) FROM 'order' WHERE parentId = :id")
    fun getCountByTableId(id : Int) : Flow<Int>

    @Query("SELECT menu FROM 'order' WHERE parentId = :id")
    fun getOrderByMenu(id : Int) : Flow<List<String>>

    @Query("SELECT id FROM 'order' ORDER BY id DESC LIMIT 1")
    fun getLastInsertOrder() : Flow<Int>

    @Query("update `order` set parentId = :first where id >= :first and id <= :last")
    fun updateFirstOrder( first: Int, last: Int)
}

