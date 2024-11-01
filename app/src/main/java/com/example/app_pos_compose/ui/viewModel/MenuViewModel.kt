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

    fun updateUiState(menuDetails: MenuDetails = MenuDetails()){
        menuUiState = MenuUiState(menuDetails = menuDetails, isEntryValid = validateInput(menuDetails))
    }

    private fun validateInput(uiState: MenuDetails = menuUiState.menuDetails) : Boolean{
        return with(uiState){
            name.isNotBlank() && price.isNotBlank()
        }
    }

    suspend fun saveMenu(){
        if(validateInput()){
            roomRepository.insertItem(menuUiState.menuDetails.toMenu())
        }
    }

    suspend fun deleteMenu(menu: Menu){
        roomRepository.deleteItem(menu)
    }

    suspend fun updateMenu(){
        if(validateInput(menuUiState.menuDetails)){
            roomRepository.updateItem(menuUiState.menuDetails.toMenu())
        }
    }
}

fun MenuDetails.toMenu(): Menu = Menu(
        id = 0,
        name = name,
        price = price
)

fun Menu.toMenuUiState(isEntryValid: Boolean = false): MenuUiState = MenuUiState(
        menuDetails = this.toMenuDetails(),
        isEntryValid = isEntryValid
)

fun Menu.toMenuDetails(): MenuDetails = MenuDetails(
        name = name,
        price = price
)

data class MenuUiState(
    val menuDetails : MenuDetails = MenuDetails(),
    val isEntryValid : Boolean = false
)

data class MenuDetails(
    val name : String = "",
    val price : String = "",
)

data class MenuListUiState(val menuList : List<Menu> = listOf())