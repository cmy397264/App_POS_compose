package com.example.app_pos_compose.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_pos_compose.data.TableSource

@Preview(showBackground = true)
@Composable
fun PosPreview(){
    POSApp()
}

enum class TabNum(i: Int) {
    Table(0),
    Receipt(1),
    Setting(2)
}

@Composable
fun POSApp(
    viewModel: UiViewModel = UiViewModel(),
    modifier: Modifier = Modifier
){
    val uiState = viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,) {
                Text(
                    text = "UFPOS",
                    modifier = modifier
                        .padding(10.dp))
            }
        }
    ) {innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if(uiState.value.isMain) TabBar(viewModel)
            when (uiState.value.currentSelectedTab) {
                TabNum.Table.ordinal -> {
                    TableUi(
                        tables = TableSource.tables,
                        viewModel = viewModel
                    )
                }

                TabNum.Receipt.ordinal -> {
                    ReceiptUi()
                }

                TabNum.Setting.ordinal -> {
                    SettingUi(
                        viewModel,
                        uiState
                    )
                }
            }
        }
    }
}

@Composable
fun TabBar(
    viewModel: UiViewModel,
    modifier: Modifier = Modifier
){
    Row(
        modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .border(0.dp, color = androidx.compose.ui.graphics.Color.Black),
        horizontalArrangement = Arrangement.SpaceEvenly

    ){
        Text(
            text = "table",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(100.dp)
                .clickable { viewModel.updateTab(TabNum.Table.ordinal) }
        )
        Text(
            text = "receipt",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(100.dp)
                .clickable { viewModel.updateTab(TabNum.Receipt.ordinal) }
        )
        Text(
            text = "setting",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(100.dp)
                .clickable { viewModel.updateTab(TabNum.Setting.ordinal) }
        )
    }
}
