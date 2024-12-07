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

    @Query("DELETE FROM `menu`")
    fun deleteAll()
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

    @Query("UPDATE 'table' SET price = :price WHERE tableNum = :tableNum")
    fun updatePriceById(tableNum: Int, price : Int)

    @Query("DELETE FROM `table`")
    fun deleteAll()
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
    fun getOrderByParentId(id : Int) : Flow<List<Order>>

    @Query("SELECT COUNT(*) FROM 'order' WHERE parentId = :id")
    fun getCountByTableId(id : Int) : Flow<Int>

    @Query("SELECT menu FROM 'order' WHERE parentId = :id")
    fun getOrderByMenu(id : Int) : Flow<List<String>>

    @Query("SELECT id FROM 'order' ORDER BY id DESC LIMIT 1")
    fun getLastInsertOrder() : Flow<Int>

    @Query("SELECT sum(price * quantity) FROM `order` WHERE parentId = :id")
    fun getPriceByParentId(id : Int) : Flow<Int>

    @Query("update `order` set parentId = :first where id >= :first and id <= :last")
    fun updateFirstOrder( first: Int, last: Int)

    @Query("DELETE FROM `order` WHERE id = (SELECT id FROM `order` WHERE menu = :menu and parentId = :parentId order by id desc limit 1)")
    fun deleteLastOrder(menu : String, parentId : Int)

    @Query("DELETE FROM `order` WHERE menu = :menu and parentId =:id")
    fun deleteOrderByMenu(menu : String, id : Int)

    @Query("UPDATE `order` SET quantity = quantity - 1 WHERE id = (SELECT id FROM `order` WHERE menu = :menu and parentId = :parentId order by id desc limit 1)")
    fun updateLastOrder(menu : String, parentId : Int)

    @Query("SELECT quantity FROM `order` WHERE menu = :menu and parentId = :parentId ORDER BY id DESC LIMIT 1")
    fun getQuantityFromLastOrder(menu : String, parentId : Int) : Flow<Int>

    @Query("SELECT * FROM `order` WHERE menu = :menu and parentId = :parentId ORDER BY id DESC LIMIT 1")
    fun getOrderByMenuAndParentId(menu : String, parentId : Int) : Flow<Order>

    @Query("SELECT * FROM `order` group by parentId order by id desc")
    fun getOrderGroupByParentId() : Flow<List<Order>>

    @Query("SELECT orderTime FROM `order` WHERE id = :firstOrder")
    fun getFirstOrderTime(firstOrder : Int) : Flow<String>

    @Query("SElECT COUNT(isDone) FROM `order` WHERE menu = :menu")
    fun getIsDoneByName(menu : String) : Flow<Int>

    @Query("UPDATE `order` SET isDone = 1 WHERE parentId = :firstOrder")
    fun updateIsDone(firstOrder : Int)

    @Query("DELETE FROM `order`")
    fun deleteAll()
}

