package com.example.app_pos_compose.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_pos_compose.data.AppViewModelProvider
import com.example.app_pos_compose.data.Order
import com.example.app_pos_compose.ui.viewModel.OrderViewModel

@Composable
fun ReceiptUi(
    orderViewModel: OrderViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    Scaffold { innerPadding ->

        val orderList by orderViewModel.orderList.collectAsState()
        val receiptList by orderViewModel.receiptList.collectAsState()
        var openReceiptDialog by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Text(
                text ="주문 내역",
                fontSize = 24.sp,
                modifier = Modifier.padding(10.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp),
            ) {
                items(receiptList.size) { index ->
                    Spacer(modifier = Modifier.height(10.dp))
                    ReceiptCard(
                        receipt = receiptList[index],
                        onClick = {
                            orderViewModel.getOrderList(receiptList[index].parentId!!)
                            orderViewModel.updateSelectedReceipt(receiptList[index])
                            openReceiptDialog = true
                        }
                    )
                }
            }
        }
        if(openReceiptDialog){
            ReceiptDialog(
                receipt = orderViewModel.uiState.selectedReceipt!!,
                orderList = orderList,
                onClickCancel = {
                    openReceiptDialog = false
                }
            )
        }
    }
}

@Composable
fun ReceiptDialog(
    receipt: Order,
    orderList: List<OrderInfo>,
    onClickCancel: () -> Unit
) {
    Dialog(
        onDismissRequest = onClickCancel,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            decorFitsSystemWindows = true
        )
    ){
        Surface(
            modifier = Modifier.width(300.dp)
                .height(380.dp),
            shape = RoundedCornerShape(10.dp)
        ){
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    text ="테이블 ${receipt.orderTable} 번",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
                OrderListUi(
                    orderList = orderList
                )
                HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 10.dp, end = 10.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${getAllPrice(orderList.toMutableList())}원",
                        fontSize = 24.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun ReceiptCard(
    receipt: Order,
    onClick: () -> Unit = {}
)
    {

    val backgroundColor = if(receipt.isDone) Color(0xFFE8F5E9) else Color(0xFFA5D6A7)
    val textColor = if(receipt.isDone) Color(0xFF606060) else Color(0xFF000000)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = textColor
        )
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
            ){
                Text(
                    text = receipt.orderTime.replace("-", "."),
                    fontSize = 24.sp,
                )

                Text(
                    text = "T${receipt.orderTable}",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ){
                Text("주문 번호 : ${receipt.parentId}")
                Text(
                    "${receipt.price}원",
                    fontSize = 18.sp
                )
            }
        }
    }
}
