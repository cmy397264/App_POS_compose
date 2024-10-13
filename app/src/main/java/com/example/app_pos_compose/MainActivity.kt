package com.example.app_pos_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.app_pos_compose.ui.POSApp
import com.example.app_pos_compose.ui.theme.App_POS_composeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App_POS_composeTheme {
                POSApp()
            }
        }
    }
}