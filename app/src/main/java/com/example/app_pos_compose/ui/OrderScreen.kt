package com.example.app_pos_compose.ui

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_pos_compose.data.AppViewModelProvider
import com.example.app_pos_compose.ui.viewModel.MenuViewModel
import com.example.app_pos_compose.ui.viewModel.OrderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderScreen(
    modifier: Modifier = Modifier,
    orderViewModel: OrderViewModel,
    tableNum: String,
    firstOrder: Int,
    onFirstOrderChange: (Int, Int) -> Unit = { _, _ ->},
    onClickSubmitButton: (Int, Int) -> Unit = { _, _ ->},
    onClickCancelButton: () -> Unit = {},
){
    val orderList = remember { mutableStateListOf<OrderInfo>() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            Column(
                modifier = Modifier.padding(bottom = 10.dp)
            ){
                FloatingActionButton(
                    modifier = Modifier.padding(bottom = 10.dp),
                    onClick = {
                        if(orderList.isNotEmpty()) {
                            coroutineScope.launch {
                                var fo = firstOrder
                                withContext(Dispatchers.IO) {
                                    orderViewModel.insertOrderFromOrderList(orderList, tableNum, fo)
                                    if (fo == 0) { //테이블의 첫 주문번호가 존재하지 않는다면
                                        // insert한 order들의 firstOrder를 처음 주문 id로 바꾸고사용한 id를 리턴
                                        fo = orderViewModel.setLastInsertOrder(orderList.size)
                                        onFirstOrderChange(tableNum.toInt(), fo)
                                    }
                                    val price = orderViewModel.getPriceById(fo)
                                    onClickSubmitButton(tableNum.toInt(), price)
                                    orderViewModel.updateOrderList(fo)
                                }

                                onClickCancelButton()
                            }
                        } else {
                            makeToast(context, "주문 목록이 비어있습니다.")
                        }
                    }
                ) {
                    Text("주문완료")
                }
                FloatingActionButton(onClick = onClickCancelButton) {
                    Text("주문취소")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier.verticalScroll(state = rememberScrollState())
                .padding(innerPadding)
        ) {
            Text(
                text = "메뉴",
                Modifier.padding(start = 10.dp)
            )
            MenuUi(onClick = { menuInfo: MenuInfo -> addOrder(orderList, menuInfo) })
            HorizontalDivider(
                thickness = 2.dp,
                color = Color(0xFFA5D6A7)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = tableNum + "번 테이블 주문 목록")
            OrderListUi(
                modifier = modifier.border(0.dp, Color.Black),
                orderList = orderList,
                onTap = {minusOrRemoveOrder(orderList, orderList[it])},
            )
            Text("총 금액 : ${getAllPrice(orderList)} 원",
                fontSize = 20.sp
            )
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
        columns = GridCells.Fixed(3),
        modifier = modifier.height(350.dp)
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
        modifier = Modifier.padding(10.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        )
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
fun OrderListUi(
    modifier: Modifier = Modifier,
    orderList: List<OrderInfo>,
    height : Int = 250,
    onTap: (Int) -> Unit = {},
) {
    OrderCellTemplate("메뉴", "수량", "가격", "금액")

    LazyColumn(
        modifier = modifier.height(height.dp)
            .border(1.dp, color = Color.Black),
    ) {
        items(orderList.size) { index ->
            val onTapToUnit = {onTap(index) }
            OrderCell(
                orderInfo = orderList[index],
                onTap = onTapToUnit,
            )
        }
    }
}

@Composable
fun OrderCellTemplate(
    firstColumn : String = "",
    secondColumn : String = "",
    thirdColumn : String = "",
    fourthColumn : String = ""
){
    Row(
        modifier = Modifier.border(
            width = 1.dp,
            color = Color.Black)
            .padding(horizontal = 10.dp)
    ) {
        Text(
            text = firstColumn,
            fontSize = 18.sp,
            modifier = Modifier.weight(3f)
        )
        Text(
            text = secondColumn,
            fontSize = 18.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = thirdColumn,
            fontSize = 18.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = fourthColumn,
            fontSize = 18.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(2f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OrderCell(
    modifier: Modifier = Modifier,
    orderInfo: OrderInfo = OrderInfo(
        MenuInfo("PreView", 5000),
        mutableIntStateOf(1)
    ),
    onTap: () -> Unit = {},
) {
    Row(
        modifier = modifier.height(48.dp)
            .padding(horizontal = 10.dp)
            .clickable { onTap() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = orderInfo.menuInfo.name,
            fontSize = 18.sp,
            modifier = modifier.weight(3f))
        Text(text = orderInfo.quantity.value.toString(),
            fontSize = 18.sp,
            textAlign = TextAlign.End,
            modifier = modifier.weight(1f))
        Text(text = orderInfo.menuInfo.price.toString(),
            fontSize = 18.sp,
            textAlign = TextAlign.End,
            modifier = modifier.weight(2f))
        Text(text = "${orderInfo.quantity.value * orderInfo.menuInfo.price}",
            fontSize = 18.sp,
            textAlign = TextAlign.End,
            modifier = modifier.weight(2f))
    }
    HorizontalDivider(
        thickness = 1.dp,
    )
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

fun makeToast(context : Context, text : String){
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}
