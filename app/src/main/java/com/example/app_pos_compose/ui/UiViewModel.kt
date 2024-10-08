package com.example.app_pos_compose.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UiViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun updateTableNum(table : Int){
        _uiState.value = _uiState.value.copy(
            currentSelectedTable = table,
            currentSelectedTab = _uiState.value.currentSelectedTab
        )
    }

    fun updateTab(tab : Int){
        _uiState.value = _uiState.value.copy(
            currentSelectedTable = _uiState.value.currentSelectedTable,
            currentSelectedTab = tab
        )
    }

    fun updateIsMain(isMain : Boolean){
        _uiState.value.isMain = isMain
    }
}