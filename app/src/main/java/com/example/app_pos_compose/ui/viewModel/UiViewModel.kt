package com.example.app_pos_compose.ui.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UiViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun updateTab(tab : Int){
        _uiState.value = _uiState.value.copy(
            currentSelectedTab = tab
        )
    }
}

data class UiState(
    var currentSelectedTab: Int = 0,
)
