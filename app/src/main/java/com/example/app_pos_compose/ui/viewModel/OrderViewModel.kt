package com.example.app_pos_compose.ui.viewModel

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import com.example.app_pos_compose.data.Order
import com.example.app_pos_compose.data.OrderRepository
import com.example.app_pos_compose.ui.MenuInfo
import com.example.app_pos_compose.ui.OrderInfo
import kotlinx.coroutines.flow.first

class OrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    suspend fun insertOrderFromOrderList(
        orderList: MutableList<OrderInfo>,
        tableNum: String,
        firstOrder: Int?
    ) {
        for (i in orderList.indices) {
            orderRepository.insertItem(orderInfoToOrder(orderList[i], tableNum, firstOrder))
        }

    }

    suspend fun setLastInsertOrder(size : Int) : Int {
        val last = getLastInsertOrder()
        val first = last - size + 1
        orderRepository.updateFirstOrder(first, last)
        return first
    }

    suspend fun getLastInsertOrder(): Int {
        return orderRepository.getLastInsertOrder().first()
    }

    suspend fun getOrderByParentTableId(n: Int): List<OrderInfo> {
        val orderList = mutableListOf<OrderInfo>()
        orderRepository.getOrderByParentTableId(n).collect {
            for (i in it.indices) {
                orderList.add(orderToOrderInfo(it[i]))
            }
        }
        return orderList
    }
}

//when Ui => ViewModel
fun orderInfoToOrder(orderInfo: OrderInfo, tableNum : String, firstOrder : Int?) : Order =
    Order(
        id = 0,
        parentId = firstOrder,
        orderTable = tableNum,
        menu = orderInfo.menuInfo.name,
        price = orderInfo.menuInfo.price.toString(),
        quantity = orderInfo.quantity.value.toString()
    )

fun orderToOrderInfo(order: Order) : OrderInfo =
    OrderInfo(
        menuInfo = MenuInfo(order.menu, order.price.toInt()),
        quantity = mutableIntStateOf(order.quantity.toInt())
    )



