package com.example.app_pos_compose.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_pos_compose.AppViewModelProvider
import com.example.app_pos_compose.data.Menu
import com.example.app_pos_compose.ui.viewModel.MenuDetails
import com.example.app_pos_compose.ui.viewModel.MenuViewModel
import com.example.app_pos_compose.ui.viewModel.TableViewModel
import com.example.app_pos_compose.ui.viewModel.toMenuDetails
import kotlinx.coroutines.launch

@Composable
fun SettingUi(
    modifier: Modifier = Modifier,
    menuViewModel: MenuViewModel = viewModel(factory = AppViewModelProvider.Factory),
    tableViewModel: TableViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val menuListUiState = menuViewModel.menuListUiState.collectAsState().value.menuList

    var openMenuSettingDialog by remember { mutableStateOf(false) }
    var openDeleteCardDialog by remember { mutableStateOf(false) }
    var isEdit by remember { mutableStateOf(false) }
    var selectedMenu by remember { mutableIntStateOf(0) }

    Box(
        modifier.fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {
        Column {
            TableSettingUi(tableViewModel = tableViewModel)
            MenuSettingUi(
                menuList = menuListUiState,
                onEditClick = { index : Int ->
                    coroutineScope.launch {
                        menuViewModel.updateUiState(menuListUiState[index].toMenuDetails())
                        isEdit = true
                        openMenuSettingDialog = true
                    }
                },
                onDeleteClick = { index : Int ->
                    selectedMenu = index
                    openDeleteCardDialog = true
                }
            )
        }
        FloatingActionButton(
            onClick = {
                menuViewModel.updateUiState()
                isEdit = false
                openMenuSettingDialog = true },
            modifier.align(Alignment.BottomEnd)
                .padding(10.dp))
        { Text("메뉴추가") }
    }

    if (openMenuSettingDialog) {
        MenuSettingDialog(
            isEdit = isEdit,
            menuDetails = menuViewModel.menuUiState.menuDetails,
            onValueChange = menuViewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    menuViewModel.saveMenu()
                    openMenuSettingDialog = false
                }
            },
            onEditClick = {
                coroutineScope.launch {
                    menuViewModel.updateMenu()
                    openMenuSettingDialog = false
                }
            },
            onCancelClick = {openMenuSettingDialog = false}
        )
    }

    if (openDeleteCardDialog) {
        DeleteAlertDialog(
            onConfirmClick = {
                coroutineScope.launch {
                    menuViewModel.deleteMenu(menuListUiState[selectedMenu])
                    openDeleteCardDialog = false
                }
            },
            onDismissClick = { openDeleteCardDialog = false }
        )
    }
}

@Composable
fun MenuSettingUi(
    menuList: List<Menu>,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
){
    Text(text = "메뉴 추가 및 삭제")
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
    ) {
        items(menuList.size) { index ->
            MenuCard(
                menu = menuList[index],
                onEditClick = {onEditClick(index)},
                onDeleteClick = {onDeleteClick(index)}
            )
        }
    }
}

@Composable
fun TableSettingUi(
    modifier: Modifier = Modifier,
    tableViewModel: TableViewModel,
){
    val tableCount = tableViewModel.tableListUiState.collectAsState().value.tableList.size

    Text(text = "테이블 추가 및 삭제")
    Row(
        modifier = modifier.fillMaxWidth()
            .height(70.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = tableViewModel.uiState.tableCount,
            onValueChange = { newCount->
                tableViewModel.updateTableCount(newCount)},
            enabled = true,
            singleLine = true,
            label = { Text("현재 테이블 수 : $tableCount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = modifier.width(200.dp)
        )
        Button(
            onClick = {
                val newCount = tableViewModel.isNullOrIntTableCount()
                val isInsert = tableCount < newCount
                tableViewModel.insertOrDeleteTable(isInsert, newCount)
            },
            enabled = tableCount.toString() != tableViewModel.uiState.tableCount && tableViewModel.uiState.tableCount != "",
            modifier = modifier
        ) {
            Text("apply")
        }
    }
}

@Composable
fun MenuSettingDialog(
    isEdit : Boolean,
    menuDetails: MenuDetails,
    onValueChange : (MenuDetails) -> Unit = {},
    onSaveClick : () -> Unit = {},
    onEditClick : () -> Unit = {},
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
                    label = { Text("메뉴 가격") },
                    enabled = true,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = if(isEdit) onEditClick else onSaveClick) {
                        Text("저장")
                    }
                    Button(onClick = onCancelClick) {
                        Text("취소")
                    }
                }
            }
        }
    }
}

@Composable
fun MenuCard(
    menu: Menu,
    modifier: Modifier = Modifier,
    onEditClick : () -> Unit = {},
    onDeleteClick : () -> Unit = {}
) {
    Card(modifier = modifier.padding(10.dp)){
        Column{
            Text(
                text = menu.name,
                modifier = modifier.fillMaxWidth()
                    .padding(start = 10.dp)
            )
            Text(
                text = menu.price,
                textAlign = TextAlign.End,
                modifier = modifier.fillMaxWidth()
                    .padding(end = 10.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteAlertDialog(
    onConfirmClick : () -> Unit,
    onDismissClick : () -> Unit
){
    Dialog(
        onDismissRequest = {  },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "정말 삭제하시는게 맞습니까?",
                modifier = Modifier.fillMaxWidth()
            )
            Row {
                Button(
                    onClick = onConfirmClick
                ) {
                    Text("확인")
                }
                Button(
                    onClick = onDismissClick
                ) {
                    Text("아니오")
                }
            }
        }
    }
}