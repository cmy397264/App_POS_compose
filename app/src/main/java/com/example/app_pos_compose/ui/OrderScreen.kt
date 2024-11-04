package com.example.app_pos_compose.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OrderScreen(
    modifier: Modifier = Modifier,
    tableNum : String,
    onCancelButtonClicked: () -> Unit = {},
){
    Box(modifier.fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {
        Column {
            Text(text = "메뉴")
            MenuUi()

            Text(text = "{${tableNum}}")
            OrderListUi(modifier = modifier)
        }
        Column(modifier.align(Alignment.BottomEnd)
            .padding(bottom = 10.dp,
                    end = 10.dp)
        ) {
            FloatingActionButton(
                onClick = onCancelButtonClicked,
                modifier.padding(bottom = 10.dp)
            ) {
                Text("주문완료")
            }

            FloatingActionButton(
                onClick = onCancelButtonClicked
            ) {
                Text("주문취소")
            }
        }
    }
}

@Composable
fun MenuUi(){
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(0.dp, color = Color.Black)
            .height(300.dp)
    ) {

    }
}

@Composable
fun OrderListUi(
    modifier: Modifier
){
    LazyRow(
        modifier = modifier
    ) {}
    Text(text = "총 원",)
}
