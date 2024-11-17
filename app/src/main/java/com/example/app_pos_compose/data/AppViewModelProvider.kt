package com.example.app_pos_compose.data

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.app_pos_compose.PosApplication
import com.example.app_pos_compose.ui.viewModel.MenuViewModel
import com.example.app_pos_compose.ui.viewModel.OrderViewModel
import com.example.app_pos_compose.ui.viewModel.TableViewModel
import com.example.app_pos_compose.ui.viewModel.UiViewModel

object AppViewModelProvider{
    val Factory = viewModelFactory {
        initializer {
            OrderViewModel(PosApplication().container.orderRepository)
        }

        initializer {
            MenuViewModel(PosApplication().container.menuRepository)
        }

        initializer {
            TableViewModel(PosApplication().container.tableRepository)
        }
        initializer {
            UiViewModel()
        }
    }
}

fun CreationExtras.PosApplication(): PosApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as PosApplication)