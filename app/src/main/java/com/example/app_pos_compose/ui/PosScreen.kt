package com.example.app_pos_compose.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app_pos_compose.data.TableSource

@Preview(showBackground = true)
@Composable
fun PosPreview(){
    POSApp()
}

enum class FloatingScreen(){
    Menu,
    Pay,
    Setting
}


@Composable
fun POSApp(
    viewModel: UiViewModel = UiViewModel(),
    modifier: Modifier = Modifier
){
    val currentSelectedTab = 0

    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Text(
            text = "UFPOS",
            modifier = modifier
                .padding(horizontal = 10.dp)
        )
        TabBar()
        when(currentSelectedTab){
            0 -> {
                TableUi(TableSource.tables, viewModel)
            }
            1 -> {
                ReceiptUi()
            }
            2 -> {
                SettingUi()
            }
        }
    }
}

@Composable
fun TabBar(
    modifier: Modifier = Modifier
){
    Row(
        modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .border(0.dp, color = androidx.compose.ui.graphics.Color.Black),
        horizontalArrangement = Arrangement.SpaceEvenly

    ){
        Tab("Table")
        Tab("receipt")
        Tab("setting")
    }
}

@Composable
fun Tab(
    title: String,
){
    Text(
        text = title,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .width(100.dp)
    )
}