package com.example.app_pos_compose

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.example.app_pos_compose.ui.MainScreen
import com.example.app_pos_compose.ui.theme.App_POS_composeTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                App_POS_composeTheme {
                    MainScreen()
                }
        }
    }
}