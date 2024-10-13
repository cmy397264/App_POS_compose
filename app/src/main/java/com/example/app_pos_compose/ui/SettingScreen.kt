package com.example.app_pos_compose.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_pos_compose.AppViewModelProvider
import com.example.app_pos_compose.data.MenuDetails
import com.example.app_pos_compose.data.MenuUiState
import com.example.app_pos_compose.data.MenuViewModel
import com.example.app_pos_compose.data.Order
import com.example.app_pos_compose.data.OrderSource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun SettingUi(
    modifier: Modifier = Modifier,
    viewModel: MenuViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    var openDialog by remember { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxSize()
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
                items(OrderSource.orders.size) { index ->
                    MenuCard(OrderSource.orders[index])
                }
            }
        }
        FloatingActionButton(
            onClick = {
                openDialog = true
            },
            modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
        ) {
            Text("메뉴추가")
        }
    }
    if (openDialog) {
        CustomDialog(
            menuDetails = viewModel.menuUiState.menuDetails,
            onValueChange = viewModel ::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveMenu()
                    openDialog = false
                }
            },
            onCancelClick = {openDialog = false}
        )
    }
}


@Composable
fun CustomDialog(
    menuDetails: MenuDetails,
    onValueChange : (MenuDetails) -> Unit = {},
    onSaveClick : () -> Unit,
    onCancelClick : () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            border = BorderStroke(1.dp, Color.Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column {
                Text("메뉴 추가 알림창")
                OutlinedTextField(
                    value = menuDetails.name,
                    onValueChange = { onValueChange(menuDetails.copy(name = it)) },
                    label = { Text("메뉴명") },
                    enabled = true,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
                OutlinedTextField(
                    value = menuDetails.price,
                    onValueChange = { onValueChange(menuDetails.copy(price = it)) },
                    label = { Text("메뉴가격") },
                    enabled = true,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )
                Row {
                    Button(
                        onClick = onSaveClick
                    ) {
                        Text("저장")
                    }
                    Button(
                        onClick = onCancelClick
                    ) {
                        Text("취소")
                    }
                }
            }
        }
    }
}

@Composable
fun MenuCard(
    order: Order,
    modifier: Modifier = Modifier
) {
    Card(
        modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Column(
            modifier.padding(horizontal = 10.dp)
        ) {
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