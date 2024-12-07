package com.example.app_pos_compose.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_pos_compose.data.AppViewModelProvider
import com.example.app_pos_compose.data.Menu
import com.example.app_pos_compose.ui.viewModel.MenuViewModel
import com.example.app_pos_compose.ui.viewModel.OrderViewModel
import com.example.app_pos_compose.ui.viewModel.TableViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SettingUi(
    menuViewModel: MenuViewModel = viewModel(factory = AppViewModelProvider.Factory),
    tableViewModel: TableViewModel = viewModel(factory = AppViewModelProvider.Factory),
    orderViewModel: OrderViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val menuListUiState = menuViewModel.menuListUiState.collectAsState().value.menuList

    var openMenuSettingDialog by remember { mutableStateOf(false) }
    var openDeleteCardDialog by remember { mutableStateOf(false) }
    var isEdit by remember { mutableStateOf(false) }
    var selectedMenu by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    menuViewModel.updateUiState()
                    isEdit = false
                    openMenuSettingDialog = true },
                modifier = Modifier.padding(bottom = 10.dp)
            ) { Text("메뉴추가") }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .verticalScroll(state = rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
                    .height(70.dp)
                    .padding(start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "데이터 초기화",
                )
                Button(
                    onClick = {
                        tableViewModel.updateTableCount("")
                        coroutineScope.launch {
                            withContext(Dispatchers.IO){
                                menuViewModel.deleteAll()
                                orderViewModel.deleteAll()
                                tableViewModel.deleteAll()
                            }
                        }
                    },
                    modifier = Modifier.height(64.dp),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text("초기화")
                }
            }

            Text(
                text = "테이블 추가 및 삭제",
                modifier = Modifier.padding(start = 10.dp)
            )
            TableSettingUi(tableViewModel = tableViewModel)
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "메뉴 추가 및 삭제",
                modifier = Modifier.padding(top = 20.dp, start = 10.dp)
            )
            HorizontalDivider(
                modifier = Modifier.padding(top = 10.dp),
                thickness = 5.dp
            )
            MenuListUi(
                menuList = menuListUiState,
                isEditable = true,
                onEditClick = { index: Int ->
                    coroutineScope.launch {
                        menuViewModel.updateUiState(menuListUiState[index])
                        isEdit = true
                        openMenuSettingDialog = true
                    }
                },
                onDeleteClick = { index: Int ->
                    selectedMenu = index
                    openDeleteCardDialog = true
                }
            )
            HorizontalDivider(
                modifier = Modifier.padding(bottom = 10.dp),
                thickness = 5.dp
            )
        }

        if (openMenuSettingDialog) {
            val menuOld : String = menuViewModel.menuUiState.menu.name

            MenuSettingDialog(
                isEdit = isEdit,
                menu = menuViewModel.menuUiState.menu,
                onValueChange = {
                    menuViewModel.updateUiState(it)
                },
                onSaveClick = {
                    coroutineScope.launch {
                        menuViewModel.saveMenu()
                        openMenuSettingDialog = false
                    }
                },
                onEditClick = {
                    coroutineScope.launch {
                        if(orderViewModel.getIsDoneByMenu(menuOld) == 0){
                            menuViewModel.updateMenu()
                        } else {
                            Toast.makeText(context, "결재 완료가 진행되지 않는 주문이 존재하는 경우 수정할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                        openMenuSettingDialog = false
                    }
                },
                onCancelClick = { openMenuSettingDialog = false }
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
}

@Composable
fun MenuListUi(
    modifier: Modifier = Modifier,
    menuList: List<Menu>,
    isEditable : Boolean = false,
    onEditClick: (Int) -> Unit = {},
    onDeleteClick: (Int) -> Unit = {},
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        items(menuList.size) { index ->
            MenuSettingCard(
                menu = menuList[index],
                isEditable = isEditable,
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
    val tableCount = tableViewModel.tableListUiState.collectAsState().value.size

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 10.dp),

        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        OutlinedTextField(
            value = tableViewModel.uiState.tableCount,
            onValueChange = { newCount->
                tableViewModel.updateTableCount(newCount)},
            enabled = true,
            singleLine = true,
            label = { Text("현재 테이블 수 : $tableCount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = modifier
                .width(200.dp)
                .fillMaxHeight()
        )
        Button(
            onClick = {
                val newCount = tableViewModel.isNullOrIntTableCount()
                val isInsert = tableCount < newCount
                tableViewModel.insertOrDeleteTable(isInsert, newCount)
            },
            shape = RoundedCornerShape(10.dp),
            enabled = tableCount.toString() != tableViewModel.uiState.tableCount && tableViewModel.uiState.tableCount != "",
            modifier = modifier
                .height(64.dp)
                .padding(top = 6.dp, start = 10.dp)
        ) {
            Text("apply")
        }
    }
}

@Composable
fun MenuSettingDialog(
    isEdit : Boolean,
    menu: Menu,
    onValueChange : (Menu) -> Unit = {},
    onSaveClick : () -> Unit = {},
    onEditClick : () -> Unit = {},
    onCancelClick : () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            decorFitsSystemWindows = true
        )
    ) {
        Surface(
            modifier = Modifier
                .width(280.dp)
                .height(210.dp),
            border = BorderStroke(1.dp, Color.Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 5.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = menu.name,
                    onValueChange = { onValueChange(menu.copy(name = it)) },
                    label = { Text("메뉴명") },
                    enabled = true,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
                OutlinedTextField(
                    value = menu.price,
                    onValueChange = { onValueChange(menu.copy(price = it)) },
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
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 5.dp),
                        onClick = {
                            if(isEdit) onEditClick() else onSaveClick()
                        }
                    ) {
                        Text("저장")
                    }
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 5.dp),
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
fun MenuSettingCard(
    modifier: Modifier = Modifier,
    menu: Menu,
    isEditable: Boolean,
    onEditClick : () -> Unit = {},
    onDeleteClick : () -> Unit = {}
) {
    Card(modifier = modifier.padding(10.dp)){
        Column{
            Text(
                text = menu.name,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            )
            Text(
                text = menu.price,
                textAlign = TextAlign.End,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp)
            )
            if(isEditable) {
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
            dismissOnClickOutside = true,
            decorFitsSystemWindows = false)
    ) {
        Surface(
            modifier = Modifier
                .width(280.dp)
                .height(100.dp),
            border = BorderStroke(1.dp, Color.Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 5.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "정말 삭제하시는게 맞습니까?",
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 5.dp),
                        onClick = onConfirmClick
                    ) {
                        Text("확인")
                    }
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 5.dp),
                        onClick = onDismissClick
                    ) {
                        Text("아니오")
                    }
                }
            }
        }
    }
}