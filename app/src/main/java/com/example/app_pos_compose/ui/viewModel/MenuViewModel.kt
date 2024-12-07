package com.example.app_pos_compose.ui.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_pos_compose.data.Menu
import com.example.app_pos_compose.data.MenuRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MenuViewModel(private val roomRepository: MenuRepository) : ViewModel() {
    var menuUiState by mutableStateOf(MenuUiState()) // 데이터 임시 저장에 사용
        private set

    val menuListUiState : StateFlow<MenuListUiState> =
        roomRepository.getAllItems().map { MenuListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = MenuListUiState()
            )

    fun updateUiState(menu: Menu = Menu()){
        menuUiState = MenuUiState(menu = menu, isEntryValid = validateInput(menu))
    }

    private fun validateInput(menu: Menu = menuUiState.menu) : Boolean{
        return with(menu){
            name.isNotBlank() && price.isNotBlank()
        }
    }

    suspend fun saveMenu(){
        if(validateInput(menuUiState.menu)){
            roomRepository.insertItem(menuUiState.menu)
        }
    }

    suspend fun deleteMenu(menu: Menu){
        roomRepository.deleteItem(menu)
    }

    suspend fun updateMenu(){
        if(validateInput(menuUiState.menu)){
            roomRepository.updateItem(menuUiState.menu)
        }
    }

    fun deleteAll(){
        roomRepository.deleteAll()
    }
}

data class MenuUiState(
    val menu : Menu = Menu(),
    val isEntryValid : Boolean = false
)

data class MenuListUiState(val menuList : List<Menu> = listOf())