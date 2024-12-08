package com.example.app_pos_compose.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class NavScreen{
    Main,
    Order,
    Pay,
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavigator(
    tableViewModel: TableViewModel = viewModel(factory = AppViewModelProvider.Factory),
    orderViewModel : OrderViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController = rememberNavController()
){
    NavHost(
        navController = navController,
        startDestination = NavScreen.Main.name,
    ){
        composable(NavScreen.Main.name){
            TableScreen(
                tableViewModel = tableViewModel,
                orderViewModel = orderViewModel,
                navController = navController
            )
        }
        composable(NavScreen.Order.name){
            OrderScreen(
                orderViewModel = orderViewModel,
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
                orderViewModel = orderViewModel,
                tableNum = tableViewModel.uiState.tableNum!!,
                firstOrder = tableViewModel.uiState.firstOrder,
                onClickSubmitButton = { tableNum : Int ->
                    tableViewModel.updatePrice(tableNum, 0)
                    tableViewModel.updateFirstOrder(tableNum, 0)
                },
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
    orderViewModel: OrderViewModel,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val listUiState by tableViewModel.tableListUiState.collectAsState()
    var openTableOrderDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if(tableViewModel.uiState.tableNum != null) {
                Column(
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    FloatingActionButton(
                        modifier = Modifier.padding(bottom = 10.dp),
                        onClick = { navController.navigate(NavScreen.Order.name) })
                    {
                        Text("주문하기")
                    }
                    FloatingActionButton(
                        onClick = {
                            orderViewModel.getFirstOrderTime(tableViewModel.uiState.firstOrder)
                            navController.navigate(NavScreen.Pay.name)
                        }
                    ) {
                        Text("결제하기")
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                Modifier.verticalScroll(state = rememberScrollState())
            ) {
                TableListUi(
                    tableList = listUiState,
                    onClick = { index ->
                        tableViewModel.updateTableNum(index)
                        coroutineScope.launch {
                            tableViewModel.setFirstOrder(index + 1)
                            orderViewModel.updateOrderList(listUiState[index].firstOrder!!)
                        }
                    }
                )
                HorizontalDivider(thickness = 2.dp, color = Color(0xFFA5D6A7))
                Spacer(modifier = Modifier.height(10.dp))
                TableInfoUi(
                    modifier = Modifier.fillMaxWidth(),
                    orderViewModel = orderViewModel,
                    tableNum = tableViewModel.uiState.tableNum,
                    onOrderClick = {openTableOrderDialog = true}
                )
                Spacer(modifier = Modifier.height(30.dp))
            }

            if(openTableOrderDialog) {
                TableOrderDialog(
                    tableViewModel = tableViewModel,
                    orderViewModel = orderViewModel,
                    coroutineScope = coroutineScope,
                    tableNum = tableViewModel.uiState.tableNum!!,
                    firstOrder = tableViewModel.uiState.firstOrder,
                    selectedOrder = orderViewModel.uiState.selectedOrder!!,
                    onClickCancel ={
                        openTableOrderDialog = false
                    },

                )
            }
        }
    }
}

@Composable
fun TableListUi(
    tableList : List<Table>,
    onClick: (Int) -> Unit,
){
    LazyVerticalGrid(
        modifier = Modifier.height(300.dp),
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
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9),
        )
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
    tableNum: String?,
    onOrderClick: () -> Unit = {}
) {
    val orderList by orderViewModel.orderList.collectAsState()

    when(tableNum){
        null ->
            Text(text = "테이블이 선택되지 않았습니다.",
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 10.dp, top = 10.dp)
            )
        else -> {
            Column(
                modifier = modifier.padding(horizontal = 10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "테이블 ${tableNum}번",
                        fontSize = 28.sp
                    )
                    Text(
                        text = "터치로 주문 수정",
                        fontSize = 14.sp,
                    )
                }
                OrderListUi(
                    orderList = orderList,
                    onTap = {
                        orderViewModel.updateSelectedOrder(orderList[it])
                        onOrderClick()
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "총 금액 : ${getAllPrice(orderList.toMutableList())} 원",
                    fontSize = 28.sp,
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TableOrderDialog(
    modifier: Modifier = Modifier,
    tableViewModel: TableViewModel,
    orderViewModel: OrderViewModel,
    coroutineScope : CoroutineScope,
    tableNum: String,
    firstOrder: Int,
    selectedOrder: OrderInfo,
    onClickCancel: () -> Unit = {},
){
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            decorFitsSystemWindows = true
        )
    ) {
        var newQuantity by remember { mutableStateOf("${selectedOrder.quantity.value}") }

        Surface(
            modifier = modifier.width(280.dp)
                .height(210.dp),
            border = BorderStroke(1.dp, Color.Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = modifier.padding(5.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "메뉴 이름 : ${selectedOrder.menuInfo.name}",
                    fontSize = 18.sp,
                    modifier = modifier.width(200.dp)
                )
                Text(
                    "메뉴 가격 : ${selectedOrder.menuInfo.price}원",
                    fontSize = 18.sp,
                    modifier = modifier.width(200.dp)
                )
                OutlinedTextField(
                    value = newQuantity,
                    onValueChange = { newQuantity = it },
                    enabled = true,
                    singleLine = true,
                    label = { Text("현재 주문 수량 : ${selectedOrder.quantity.value}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = modifier.width(200.dp)
                )
                Row(
                    modifier.padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                                if(newQuantity.toInt() != selectedOrder.quantity.value) {
                                    coroutineScope.launch {
                                        withContext(Dispatchers.IO) {
                                            if (newQuantity.toInt() == 0) {//quantity를 0으로 설정하면 해당 테이블에서 입력된 메뉴를 전부 삭제한다.
                                                orderViewModel.deleteAllOrderByName(
                                                    selectedOrder.menuInfo.name,
                                                    firstOrder
                                                )
                                            } else if (newQuantity.toInt() > selectedOrder.quantity.value) {//기존 quantity보다 수정값이 더 높다면 주문을 추가한다.
                                                orderViewModel.insertOrder(
                                                    selectedOrder,
                                                    newQuantity.toInt() - selectedOrder.quantity.value,
                                                    tableNum,
                                                    firstOrder
                                                )
                                            } else {//기존 quantity보다 수정값이 낮다면 마지막 주문을 불러와 quantity를 불러온다
                                                var order =
                                                    orderViewModel.getOrderByMenuAndParentId(
                                                        selectedOrder.menuInfo.name,
                                                        firstOrder
                                                    )
                                                while (newQuantity.toInt() - order.quantity >= 0) {//불러온 값이 수정값보다 작다면
                                                    newQuantity =
                                                        (newQuantity.toInt() - order.quantity).toString() //수정값을 불러온 값만큼 감소시키고
                                                    orderViewModel.deleteOrder(order)//해당 테이블에서 마지막으로 주문된 주문을 삭제한다.
                                                    order =
                                                        orderViewModel.getOrderByMenuAndParentId(
                                                            selectedOrder.menuInfo.name,
                                                            firstOrder
                                                        )//마지막 주문을 새로 불러온다.
                                                }
                                                order.quantity = newQuantity.toInt()
                                                orderViewModel.updateOrder(order)
                                            }
                                            val price = orderViewModel.getPriceById(firstOrder)
                                            if(price == 0){
                                                tableViewModel.updateFirstOrder(tableNum.toInt(), 0)
                                            }
                                            tableViewModel.updatePrice(tableNum.toInt(), price)
                                            orderViewModel.updateOrderList(firstOrder)
                                        }
                                    }
                                }
                            onClickCancel()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 5.dp),
                    ){
                        Text("수정 완료")
                    }
                    Button(
                        onClick = onClickCancel,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 5.dp),
                    ) {
                        Text("수정 취소")
                    }
                }
            }
        }
    }
}


