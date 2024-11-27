package com.example.app_pos_compose.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_pos_compose.data.AppViewModelProvider
import com.example.app_pos_compose.ui.viewModel.OrderViewModel
import kotlin.reflect.KFunction1

@Composable
fun PayScreen(
    orderViewModel: OrderViewModel = viewModel(factory = AppViewModelProvider.Factory),
    tableNum: String,
    firstOrder: Int,
    onClickSubmitButton: KFunction1<Int, Unit>,
    onClickCancelButton: () -> Unit
){
    val orderList = orderViewModel.orderList.collectAsState().value
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("$tableNum 번")
            TableOrderListUi(
                modifier = Modifier,
                firstOrder = firstOrder,
                orderList = orderList,
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = 60.dp,
                    end = 10.dp)
        ) {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 10.dp),
                onClick = {onClickSubmitButton(tableNum.toInt())
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
}