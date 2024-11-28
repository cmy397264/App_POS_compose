package com.example.app_pos_compose.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app_pos_compose.data.AppViewModelProvider
import com.example.app_pos_compose.data.Table
import com.example.app_pos_compose.ui.viewModel.OrderViewModel
import com.example.app_pos_compose.ui.viewModel.TableViewModel
import kotlinx.coroutines.launch

enum class NavScreen{
    Main,
    Order,
    Pay,
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavigator(
    tableViewModel: TableViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController = rememberNavController()
){
    NavHost(
        navController = navController,
        startDestination = NavScreen.Main.name,
    ){
        composable(NavScreen.Main.name){
            TableScreen(
                tableViewModel = tableViewModel,
                navController = navController
            )
        }
        composable(NavScreen.Order.name){
            OrderScreen(
                tableNum = tableViewModel.uiState.tableNum!!,
                firstOrder = tableViewModel.uiState.firstOrder,
                onFirstOrderChange = { tableNum : Int, firstOrder : Int ->
                    tableViewModel.updateFirstOrder(tableNum, firstOrder)
                    tableViewModel.changeFirstOrder(firstOrder) },
                onClickSubmitButton = { tableNum, price : Int ->
                    tableViewModel.updatePrice(tableNum, price) },
                onClickCancelButton = {
                    navController.popBackStack(NavScreen.Main.name, inclusive = false) }
            )
        }
        composable(NavScreen.Pay.name){
            PayScreen(
                tableNum = tableViewModel.uiState.tableNum!!,
                firstOrder = tableViewModel.uiState.firstOrder,
                onClickSubmitButton = tableViewModel :: resetTable,
                onClickCancelButton = {
                    navController.popBackStack(NavScreen.Main.name, inclusive = false)
                },
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TableScreen(
    tableViewModel: TableViewModel,
    orderViewModel: OrderViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val listUiState = tableViewModel.tableListUiState.collectAsState().value.tableList

    Column(
        Modifier.scrollable(
            state = ScrollState(0),
            orientation = Orientation.Vertical,
        )
    ) {
        TableListUi(
            tableList = listUiState,
            onClick = { index ->
                tableViewModel.updateTableNum(index)
                coroutineScope.launch {
                    tableViewModel.setFirstOrder(index + 1)
                    orderViewModel.getOrderList(listUiState[index].firstOrder!!)
                }
            }
        )
        TableInfoUi(
            modifier = Modifier.fillMaxWidth(),
            orderViewModel = orderViewModel,
            firstOrder = tableViewModel.uiState.firstOrder,
            tableNum = tableViewModel.uiState.tableNum,
            onClickOrder = {navController.navigate(NavScreen.Order.name)},
            onClickPay = {navController.navigate(NavScreen.Pay.name)},
            onPriceChange = {
                tableNum : Int, price : Int ->
                tableViewModel.updatePrice(tableNum, price) // 오류 발생
            }
        )
    }
}

@Composable
fun TableListUi(
    tableList : List<Table>,
    onClick: (Int) -> Unit,
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(10.dp)
    ) {
        items(tableList.size) { index ->
            TableCard(
                title = tableList[index].tableNum.toString() + "번",
                context = tableList[index].price + "원",
                onClick = {
                    onClick(index)
                }
            )
        }
    }
}

@Composable
fun TableCard(
    title : String,
    context : String,
    onClick: () -> Unit,
){
    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .wrapContentHeight()
        ){
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TableInfoUi(
    modifier: Modifier = Modifier,
    orderViewModel: OrderViewModel,
    firstOrder: Int,
    tableNum: String?,
    onClickOrder: () -> Unit,
    onClickPay: () -> Unit,
    onPriceChange: (Int, Int) -> Unit
) {
    val orderList = orderViewModel.orderList.collectAsState().value

    when(tableNum){
        null ->
            Text(text = "테이블이 선택되지 않았습니다.")
        else -> {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(modifier.padding(horizontal = 10.dp)) {
                    Row{
                        Text(
                            text = "테이블 ${tableNum}번",
                            fontSize = 28.sp
                        )
                        Column(modifier) {
                            Text(
                                text = "한번 터치 = 메뉴 추가",
                                fontSize = 14.sp,
                                modifier = Modifier.align(Alignment.End)
                            )
                            Text(
                                text = "두번 터치 = 메뉴 삭제",
                                fontSize = 14.sp,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                    TableOrderListUi(
                        firstOrder = firstOrder,
                        orderList = orderList,
                        onTap = { index : Int ->
                            orderViewModel.insertOrderFromOrderInfo(orderList[index], tableNum, firstOrder)
                        },
                        onDoubleTap = { menu : String, parentId : Int ->
                            orderViewModel.deleteOrUpdateOrder(menu, parentId)
                        },
                        onPriceChange = { price : Int ->
                            onPriceChange(tableNum.toInt(), price)
                        }
                    )
                    Text(
                        text = "총원 : ${getAllPrice(orderList.toMutableList())} 원",
                        fontSize = 28.sp
                    )
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            bottom = 10.dp,
                            end = 10.dp
                        )
                ) {
                    FloatingActionButton(
                        onClick = onClickOrder,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text("주문하기")
                    }

                    FloatingActionButton(
                        onClick = onClickPay,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text("결제하기")
                    }
                }
            }
        }
    }
}

@Composable
fun TableOrderListUi(
    modifier: Modifier = Modifier,
    firstOrder : Int,
    orderList: List<OrderInfo>,
    onTap: (Int) -> Unit = {},
    onDoubleTap: (String, Int) -> Unit = { _: String, _: Int -> },
    onPriceChange: (Int) -> Unit = {}
) {
    OrderCellTemplate("메뉴", "수량", "가격", "금액")

    LazyColumn(
        modifier.defaultMinSize(minHeight = 200.dp)
    ) {
        items(orderList.size) { index ->
            val onTapToUnit = {
                onTap(index)
                onPriceChange(orderList[index].menuInfo.price)
            }
            val onDoubleTapToUnit = {
                if (firstOrder != 0) {
                    onDoubleTap(orderList[index].menuInfo.name, firstOrder)
                    onPriceChange(orderList[index].menuInfo.price * -1)
                }
            }
            TableOrderCell(
                orderInfo = orderList[index],
                onTap = onTapToUnit,
                onDoubleTap = onDoubleTapToUnit
            )
        }
    }
}

@Composable
fun TableOrderCell(
    modifier: Modifier = Modifier,
    orderInfo: OrderInfo,
    onTap: () -> Unit = {},
    onDoubleTap: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { _ -> onTap() },
                    onDoubleTap = { _ -> onDoubleTap() }
                )
            }
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


