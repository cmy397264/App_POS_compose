package com.example.app_pos_compose.ui.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_pos_compose.data.Order
import com.example.app_pos_compose.data.OrderRepository
import com.example.app_pos_compose.ui.MenuInfo
import com.example.app_pos_compose.ui.OrderInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    private val _orderList = MutableStateFlow<List<OrderInfo>>(listOf())
    val orderList : StateFlow<List<OrderInfo>> = _orderList.asStateFlow()

    fun getOrderList(firstOrder : Int) {
        viewModelScope.launch {
            orderRepository.getOrderByParentId(firstOrder).collect { orders ->
                _orderList.value = orders.groupBy {
                    it.menu
                }.map{ (menu, order) ->
                    OrderInfo(
                        menuInfo = MenuInfo(menu, order[0].price.toInt()),
                        quantity = mutableIntStateOf(order.sumOf { it.quantity})
                    )
                }
            }
        }
    }

    fun deleteOrUpdateOrder(menu: String, parentId: Int){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val quantity = orderRepository.getQuantityFromLastOrder(menu, parentId).first()
                if (quantity > 1) {
                    orderRepository.updateLastOrder(menu, parentId)
                } else {
                    orderRepository.deleteLastOrder(menu, parentId)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun insertOrderFromOrderInfo(
        orderInfo: OrderInfo,
        tableNum: String,
        firstOrder: Int?
    ){
        viewModelScope.launch {
            orderRepository.insertItem(
                orderInfoToOrder(
                    OrderInfo(
                        orderInfo.menuInfo,
                        mutableIntStateOf(1)
                    ), tableNum, firstOrder))
        }
    }

    //UI에서 사용
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertOrderFromOrderList(
        orderList: MutableList<OrderInfo>,
        tableNum: String,
        firstOrder: Int?
    ) {
        for (i in orderList.indices) {
            orderRepository.insertItem(orderInfoToOrder(orderList[i], tableNum, firstOrder))
        }
    }
    
    //UI에서 사용
    suspend fun setLastInsertOrder(size : Int) : Int {
        val last = getLastInsertOrder()
        val first = last - size + 1
        orderRepository.updateFirstOrder(first, last)
        return first
    }

    private suspend fun getLastInsertOrder(): Int {
        return orderRepository.getLastInsertOrder().first()
    }
}

//LocalDateTime 사용
@RequiresApi(Build.VERSION_CODES.O)
fun orderInfoToOrder(orderInfo: OrderInfo, tableNum : String, firstOrder : Int?) : Order =
    Order(
        id = 0,
        parentId = firstOrder,
        orderTable = tableNum,
        menu = orderInfo.menuInfo.name,
        price = orderInfo.menuInfo.price.toString(),
        quantity = orderInfo.quantity.value,
        orderTime = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    )



