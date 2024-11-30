package com.example.app_pos_compose.ui.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_pos_compose.data.Order
import com.example.app_pos_compose.data.OrderRepository
import com.example.app_pos_compose.ui.MenuInfo
import com.example.app_pos_compose.ui.OrderInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    private val _orderList = MutableStateFlow<List<OrderInfo>>(listOf())
    val orderList : StateFlow<List<OrderInfo>> = _orderList.asStateFlow()

    var uiState by mutableStateOf(OrderUiState())

    fun getOrderList(firstOrder : Int) {
        orderRepository.getOrderByParentId(firstOrder).onEach { orders ->
            _orderList.value = orders.groupBy {
                it.menu
            }.map { (menu, order) ->
                OrderInfo(
                    menuInfo = MenuInfo(menu, order[0].price.toInt()),
                    quantity = mutableIntStateOf(order.sumOf { it.quantity })
                )
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun insertOrder(
        orderInfo: OrderInfo,
        quantity: Int,
        tableNum: String,
        firstOrder: Int?
    ){
        viewModelScope.launch {
            orderRepository.insertItem(
                orderInfoToOrder(
                    OrderInfo(
                        orderInfo.menuInfo,
                        mutableIntStateOf(quantity)
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

    suspend fun getOrderByMenuAndParentId(menu : String, parentId : Int) : Order {
        return orderRepository.getOrderByMenuAndParentId(menu, parentId).first()
    }

    fun updateSelectedOrder(orderInfo: OrderInfo){
        uiState = uiState.copy(
            selectedOrder = orderInfo
        )
    }

    fun deleteOrder(order: Order){
        viewModelScope.launch {
            orderRepository.deleteItem(order)
        }
    }

    fun deleteByName(menu : String, firstOrder : Int){
        orderRepository.deleteOrderByMenu(menu, firstOrder)
    }

    fun updateOrder(order: Order){
        viewModelScope.launch {
            orderRepository.updateItem(order)
        }
    }

    fun getFirstOrderTime(firstOrder : Int){
        viewModelScope.launch {
            uiState = uiState.copy(
                timeOfFirstOrder = orderRepository.getFirstOrderTime(firstOrder).first()
            )
        }
    }
}

data class OrderUiState(
    val selectedOrder : OrderInfo? = null,
    val timeOfFirstOrder : String? = null
)


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



