package com.example.app_pos_compose.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app_pos_compose.data.OrderSource
import com.example.app_pos_compose.data.Table

enum class NavScreen{
    Main,
    Order,
    Pay,
}

@Composable
fun TableUi(
    tables : List<Table>,
    viewModel: UiViewModel,
    navController: NavHostController = rememberNavController()
){
    val uiState by viewModel.uiState.collectAsState()
    NavHost(
        navController = navController,
        startDestination = NavScreen.Main.name,
    ){
        composable(NavScreen.Main.name){
            TableList(
                tables = tables,
                viewModel = viewModel,
                uiState = uiState,
                navController = navController)
        }
        composable(NavScreen.Order.name){
            MenuUi(
                uiState = uiState,
                onCancelButtonClicked = { navController.popBackStack(NavScreen.Main.name, inclusive = false) }
            )
        }
        composable(NavScreen.Pay.name){

        }
    }
}

@Composable
fun TableList(
    tables: List<Table>,
    viewModel: UiViewModel,
    uiState: UiState,
    navController: NavController
) {
    Column{
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(10.dp)
        ) {
            items(tables.size) { index ->
                TableCard(table = tables[index], viewModel)
            }
        }
        TableInfoUi(uiState.currentSelectedTable, navController)
    }
}

@Composable
fun TableCard(
    table: Table,
    viewModel: UiViewModel
){
    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable(
                onClick = {
                    viewModel.updateTableNum(table.id)
                }
            ),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .wrapContentHeight()
        ){
            Text(
                text = "${table.id.toString()}번",
                textAlign = TextAlign.Start,
                modifier = Modifier.wrapContentWidth()
            )
            Text(
                text = "$" + table.price,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TableInfoUi(
    currentSelectedTable: Int?,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    when (currentSelectedTable) {
        null -> Text(
            text = "테이블이 선택되지 않았습니다.",
            modifier.padding(10.dp)
        )
        else -> {
            Box(
                modifier.fillMaxSize()
            ) {
                Column(
                    modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    Text(text = "테이블 ${currentSelectedTable}번")
                    LazyColumn(
                        modifier
                            .height(150.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .border(
                                1.dp,
                                color = androidx.compose.ui.graphics.Color.Gray,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                            ),
                        horizontalAlignment = Alignment.Start
                    ) {
                        items(OrderSource.orders.size) { index ->
                            Text(text = OrderSource.orders[index].menu)
                        }
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
                            navController.navigate(NavScreen.Order.name)
                        },
                        modifier
                            .padding(10.dp)
                    ) {
                        Text("주문하기")
                    }

                    FloatingActionButton(
                        onClick = {
                            navController.navigate(NavScreen.Pay.name)
                        },
                        modifier
                            .padding(10.dp)
                    ) {
                        Text("결제하기")
                    }
                }

                Text(
                    text = "총 ${OrderSource.orders.size} 원",
                    modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            start = 30.dp,
                            bottom = 30.dp
                        ),
                )
            }
        }
    }
}