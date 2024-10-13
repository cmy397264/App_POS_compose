package com.example.app_pos_compose.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MenuViewModel(private val roomRepository: RoomRepository) : ViewModel() {
    var menuUiState by mutableStateOf(MenuUiState())
        private set

    fun updateUiState(menuDetails: MenuDetails){
        menuUiState = MenuUiState(menuDetails = menuDetails, isEntryValid = validateInput(menuDetails))
    }

    private fun validateInput(uiState: MenuDetails = menuUiState.menuDetails) : Boolean{
        return with(uiState){
            name.isNotBlank() && price.isNotBlank()
        }
    }

    suspend fun saveMenu(){
        if(validateInput()){
            roomRepository.insertMenu(menuUiState.menuDetails.toMenu())
        }
    }
}

fun MenuDetails.toMenu(): Menu = Menu(
        id = id,
        name = name,
        price = price
)

fun Menu.toMenuUiState(isEntryValid: Boolean = false): MenuUiState = MenuUiState(
        menuDetails = this.toMenuDetails(),
        isEntryValid = isEntryValid
)

fun Menu.toMenuDetails(): MenuDetails = MenuDetails(
        id = id,
        name = name,
        price = price
)


data class MenuUiState(
    val menuDetails : MenuDetails = MenuDetails(),
    val isEntryValid : Boolean = false
)

data class MenuDetails(
    val id : Int = 0,
    val name : String = "",
    val price : String = "",
)