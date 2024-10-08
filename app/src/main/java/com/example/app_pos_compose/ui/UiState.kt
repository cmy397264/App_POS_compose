package com.example.app_pos_compose.ui

data class UiState(
    var currentSelectedTab: Int = 0,
    var currentSelectedTable : Int? = null,
    var menuCount : Int = 0,
    var isMain : Boolean = true
)