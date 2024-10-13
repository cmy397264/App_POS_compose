package com.example.app_pos_compose

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app_pos_compose.data.MenuViewModel
import com.example.app_pos_compose.ui.UiViewModel

object AppViewModelProvider{
    val Factory = viewModelFactory {
        initializer {
            MenuViewModel(menuApplication().container.roomRepository)
        }

        initializer {
            UiViewModel()
        }
    }
}

fun CreationExtras.menuApplication(): MenuApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MenuApplication)