package com.example.app_pos_compose.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_pos_compose.data.AppViewModelProvider
import com.example.app_pos_compose.ui.viewModel.UiViewModel

enum class TabNum {
    Table,
    Receipt,
    Setting
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    uiViewModel: UiViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val uiState = uiViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "UFPOS",
                        modifier = modifier
                            .padding(10.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .height(48.dp)
                        .border(1.dp, color = Color.Black),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(
                        text = "table",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(120.dp)
                            .height(40.dp)
                            .clickable { uiViewModel.updateTab(TabNum.Table.ordinal) }
                            .padding(vertical = 8.dp)
                    )
                    VerticalDivider(modifier = Modifier.height(48.dp))
                    Text(
                        text = "receipt",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(120.dp)
                            .height(40.dp)
                            .clickable { uiViewModel.updateTab(TabNum.Receipt.ordinal) }
                            .padding(vertical = 8.dp)
                    )
                    VerticalDivider(modifier = Modifier.height(48.dp))
                    Text(
                        text = "setting",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(120.dp)
                            .height(40.dp)
                            .clickable { uiViewModel.updateTab(TabNum.Setting.ordinal) }
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    ) {innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (uiState.value.currentSelectedTab) {
                TabNum.Table.ordinal -> {
                    MainNavigator()
                }

                TabNum.Receipt.ordinal -> {
                    ReceiptUi()
                }

                TabNum.Setting.ordinal -> {
                    SettingUi()
                }
            }
        }
    }
}
