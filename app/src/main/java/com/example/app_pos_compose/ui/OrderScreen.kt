package com.example.app_pos_compose.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_pos_compose.data.AppViewModelProvider
import com.example.app_pos_compose.ui.viewModel.MenuViewModel
import com.example.app_pos_compose.ui.viewModel.OrderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun OrderScreen(
    modifier: Modifier = Modifier,
    orderViewModel: OrderViewModel = viewModel(factory = AppViewModelProvider.Factory),
    tableNum: String,
    firstOrder: Int?,
    onFirstOrderChange: (Int, Int) -> Unit = { _, _ ->},
    onClickSubmitButton: (Int, Int) -> Unit = { _, _ ->},
    onClickCancelButton: () -> Unit = {},
){
    val orderList = remember { mutableStateListOf<OrderInfo>() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {
        Column {
            Text(text = "메뉴")
            MenuUi(onClick = { menuInfo: MenuInfo -> addOrder(orderList, menuInfo) })
            Text(text = tableNum + "번")
            Text(text = "주문 목록")
            OrderCellTemplate()
            LazyColumn(
                modifier = modifier.height(200.dp)
                    .border(1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp))
            ) {
                items(orderList.size) { index ->
                    OrderCell(
                        orderInfo = orderList[index],
                        onDelete = { minusOrRemoveOrder(orderList, orderList[index]) }
                    )
                }
            }
            if (orderList.isNotEmpty()) {
                Text(
                    text = "총 금액 : ${getAllPrice(orderList)} 원",
                    modifier = modifier.padding(top = 20.dp, start = 20.dp)
                )
            }
        }
        Column(
            modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = 10.dp,
                    end = 10.dp
                )
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            if (orderList.isNotEmpty()) {
                                orderViewModel.insertOrderFromOrderList(
                                    orderList, tableNum, firstOrder)
                            }

                            if(firstOrder == 0 || firstOrder == null) {
                                val fo = orderViewModel.setLastInsertOrder(orderList.size)
                                onFirstOrderChange(fo, tableNum.toInt())
                            }

                            onClickSubmitButton(tableNum.toInt(), getAllPrice(orderList).toInt())
                        }

                        withContext(Dispatchers.Main) {
                            onClickCancelButton()
                        }
                    }
                },
                modifier.padding(bottom = 50.dp)
            ) {
                Text("주문완료")
            }

            FloatingActionButton(
                onClick = onClickCancelButton,
                modifier.padding(bottom = 40.dp)
            ) {
                Text("주문취소")
            }
        }
    }
}

@Composable
fun MenuUi(
    modifier: Modifier = Modifier,
    menuViewModel: MenuViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onClick: (MenuInfo) -> Unit
){
    val menuList = menuViewModel.menuListUiState.collectAsState().value.menuList

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier.height(300.dp)
    ) {
        items(menuList.size) { index ->
            MenuCard(
                title = menuList[index].name,
                context = menuList[index].price + "원",
                onClick = {
                    val menuInfo = MenuInfo(menuList[index].name, menuList[index].price.toInt())
                    onClick(menuInfo)
                }
            )
        }
    }
}

@Composable
fun MenuCard(
    title : String,
    context : String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .wrapContentHeight()
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Start,
                modifier = Modifier.wrapContentWidth()
            )
            Text(
                text = context,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun OrderCellTemplate(
    modifier: Modifier = Modifier
){
    Row {
        Text(
            text = "메뉴",
            modifier = modifier.weight(2f)
        )
        Text(
            text = "수량",
            modifier = modifier.weight(1f)
        )
        Text(
            text = "가격",
            modifier = modifier.weight(1f)
        )
        Text(
            text = "금액",
            modifier = modifier.weight(1f)
        )
    }
}

@Composable
fun OrderCell(
    modifier: Modifier = Modifier,
    orderInfo: OrderInfo,
    onDelete: () -> Unit
) {
    Row(
        modifier = modifier.padding(horizontal = 10.dp)
            .clickable(onClick = onDelete)
    ) {
        Text(text = orderInfo.menuInfo.name,
            modifier = modifier.weight(2f))
        Text(text = orderInfo.quantity.value.toString(),
                modifier = modifier.weight(1f))
        Text(text = orderInfo.menuInfo.price.toString(),
            modifier = modifier.weight(1f))
        Text(text = "${orderInfo.quantity.value * orderInfo.menuInfo.price}",
            modifier = modifier.weight(1f))
    }
}

data class MenuInfo(
    val name : String,
    val price : Int
)

data class OrderInfo(
    val menuInfo : MenuInfo,
    var quantity : MutableState<Int> = mutableIntStateOf(0),
)

fun getAllPrice(orderList : MutableList<OrderInfo>) : String {
    var total = 0
    for(i in orderList.indices){
        total += orderList[i].quantity.value * orderList[i].menuInfo.price
    }
    return total.toString()
}

fun addOrder(orderList : MutableList<OrderInfo>, menuInfo : MenuInfo){
    for(i in orderList.indices){
        if(orderList[i].menuInfo == menuInfo){
            orderList[i].quantity.value += 1
            return
        }
    }
    orderList.add(OrderInfo(menuInfo, mutableIntStateOf(1)))
}

fun minusOrRemoveOrder(orderList: MutableList<OrderInfo>, orderInfo: OrderInfo) {
    if(orderInfo.quantity.value > 1) {
        orderInfo.quantity.value -= 1
        return
    }
    for (i in orderList.indices) {
        if (orderList[i] == orderInfo) {
            orderList.removeAt(i)
            return
        }
    }
}
