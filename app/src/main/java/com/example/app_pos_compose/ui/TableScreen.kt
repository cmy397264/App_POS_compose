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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app_pos_compose.data.AppViewModelProvider
import com.example.app_pos_compose.data.Table
import com.example.app_pos_compose.ui.viewModel.TableViewModel

enum class NavScreen{
    Main,
    Order,
    Pay,
}

@Composable
fun MainNavigator(
    viewModel: TableViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController = rememberNavController()
){
    NavHost(
        navController = navController,
        startDestination = NavScreen.Main.name,
    ){
        composable(NavScreen.Main.name){
            TableScreen(
                tableViewModel = viewModel,
                navController = navController
            )
        }
        composable(NavScreen.Order.name){
            OrderScreen(
                tableNum = viewModel.getTableNum(),
                firstOrder = viewModel.getFirstOrder(),
                onFirstOrderChange = {
                    tableNum: Int, firstOrder : Int -> viewModel.updateFirstOrder(tableNum, firstOrder) },
                onClickSubmitButton = {
                    tableNum: Int, price : Int -> viewModel.updatePrice(tableNum, price) },
                onClickCancelButton = {
                    navController.popBackStack(NavScreen.Main.name, inclusive = false) }
            )
        }
        composable(NavScreen.Pay.name){

        }
    }
}

@Composable
fun TableScreen(
    tableViewModel: TableViewModel,
    navController: NavController
) {
    val listUiState = tableViewModel.tableListUiState.collectAsState().value.tableList
    Column {
        TableListUi(
            tableList = listUiState,
            onClick = {index -> tableViewModel.updateTableNum(index)}
        )
        TableInfoUi(
            tableNum = tableViewModel.uiState.selectedTableNum,
            navController = navController
        )
    }
}

@Composable
fun TableListUi(
    tableList : List<Table>,
    onClick: (Int) -> Unit
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(10.dp)
    ) {
        items(tableList.size) { index ->
            TableCard(
                title = tableList[index].tableNum.toString() + "번",
                context = tableList[index].price + "원",
                onClick = {onClick(index)}
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

@Composable
fun TableInfoUi(
    tableNum: String?,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    when(tableNum){
        null ->
            Text(text = "테이블이 선택되지 않았습니다.")
        else -> {
            Box(
                modifier.fillMaxSize()
            ) {
                Column(
                    modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    Text(text = "테이블 ${tableNum}번")
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
//                        items() { index ->
//                        }
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
            }
        }
    }
}