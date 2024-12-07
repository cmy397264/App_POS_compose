package com.example.app_pos_compose.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_pos_compose.ui.viewModel.OrderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PayScreen(
    orderViewModel: OrderViewModel,
    tableNum: String,
    firstOrder: Int,
    onClickSubmitButton: (Int) -> Unit,
    onClickCancelButton: () -> Unit
){
    val orderList by orderViewModel.orderList.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = { Column(
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                FloatingActionButton(
                    modifier = Modifier.padding(bottom = 10.dp),
                    onClick = {
                        coroutineScope.launch {
                            withContext(Dispatchers.IO) {
                                onClickSubmitButton(tableNum.toInt())
                                orderViewModel.updateIsDone(firstOrder)
                                orderViewModel.getOrderList(0)
                            }
                            withContext(Dispatchers.Main){
                                onClickCancelButton()
                            }
                        }
                    }
                ) {
                    Text("결제완료")
                }
                FloatingActionButton(
                    onClick = onClickCancelButton,
                ) {
                    Text("결제취소")
                }
            }
        }
    )
    { innerPadding ->
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(innerPadding)
        ) {
            Text("${tableNum}번 테이블 주문 내역",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
            )
            OrderListUi(
                modifier = Modifier,
                orderList = orderList,
                height = 480
            )
            HorizontalDivider(thickness = 2.dp)
            Text("총 ${getAllPrice(orderList.toMutableList())}원",
                textAlign = TextAlign.End,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth()
                    .padding(end = 20.dp)
            )
            Row() {
                Text("테이블 이용 시작 시간 : ")
                Text("${orderViewModel.uiState.timeOfFirstOrder}")
            }
        }
    }
}