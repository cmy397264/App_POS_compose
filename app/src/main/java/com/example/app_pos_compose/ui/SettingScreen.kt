package com.example.app_pos_compose.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.app_pos_compose.data.Order
import com.example.app_pos_compose.data.OrderSource

@Composable
fun SettingUi(
    viewModel: UiViewModel,
    uiState: State<UiState>,
    modifier: Modifier = Modifier
){
    Box(
        modifier.fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {
        Column {
            Text(
                text = "메뉴 추가 및 삭제"
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .border(0.dp, color = Color.Black)
            ) {
                items(OrderSource.orders.size){ index ->
                    MenuCard(OrderSource.orders[index])
                }
            }
        }
        FloatingActionButton(
            onClick = {

            },
            modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
        ) {
            Text("메뉴추가")
        }
    }
}

@Composable
fun MenuCard(
    order: Order,
    modifier: Modifier = Modifier) {
    Card(
        modifier
            .fillMaxWidth()
            .padding(10.dp)
    ){
        Column(
            modifier.padding(horizontal = 10.dp)
        ){
            Text(
                text = order.menu
            )
            Text(
                text = order.price
            )
            Row(
                modifier,
                horizontalArrangement = Arrangement.SpaceBetween
                ) {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null
                    )
                }
            }
        }
    }
}
