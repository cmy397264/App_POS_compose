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
    override fun getFirstOrder(tableNum : Int) : Flow<Int> = tableDao.getFirstOrder(tableNum)
    override fun updateFirstOrderById(tableNum: Int, firstOrder: Int) = tableDao.updateFirstOrderById(tableNum, firstOrder)
    override fun updatePriceById(tableNum: Int, price : Int) = tableDao.updatePriceById(tableNum, price)
}

class OrderRepository(private val orderDao: OrderDao) : OrderRepo {
    override suspend fun insertItem(item: Order) = orderDao.insertOrder(item)

    override suspend fun updateItem(item: Order) = orderDao.updateOrder(item)

    override suspend fun deleteItem(item: Order) = orderDao.deleteOrder(item)

    override fun getOrderByParentId(n: Int) : Flow<List<Order>> = orderDao.getOrderByParentId(n)

    override fun getOrderByMenu(n: Int): Flow<List<String>> = orderDao.getOrderByMenu(n)

    override fun getCountByTableId(n : Int) : Flow<Int> = orderDao.getCountByTableId(n)

    override fun getLastInsertOrder() : Flow<Int> = orderDao.getLastInsertOrder()
    override fun updateFirstOrder(first : Int, last: Int) = orderDao.updateFirstOrder(first, last)

    override fun deleteLastOrder(menu: String, parentId: Int) = orderDao.deleteLastOrder(menu, parentId)

    override fun deleteOrderByMenu(menu: String, id: Int) = orderDao.deleteOrderByMenu(menu, id)

    override fun updateLastOrder(menu: String, parentId: Int) = orderDao.updateLastOrder(menu, parentId)

    override fun getQuantityFromLastOrder(menu: String, parentId: Int): Flow<Int> = orderDao.getQuantityFromLastOrder(menu, parentId)

    override fun getOrderByMenuAndParentId(menu: String, parentId: Int): Flow<Order> = orderDao.getOrderByMenuAndParentId(menu, parentId)

    override fun getFirstOrderTime(firstOrder: Int): Flow<String> = orderDao.getFirstOrderTime(firstOrder)
}