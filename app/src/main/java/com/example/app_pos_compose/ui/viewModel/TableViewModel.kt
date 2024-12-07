package com.example.app_pos_compose.ui.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_pos_compose.data.Table
import com.example.app_pos_compose.data.TableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TableViewModel(private val roomRepository: TableRepository) : ViewModel() {
    val tableListUiState: StateFlow<List<Table>> =
        roomRepository.getAllItems().map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = listOf()
            )
    var uiState by mutableStateOf(TableUiState())
    
    
    //UI함수에서 호출
    fun updateFirstOrder(tableNum: Int, firstOrder: Int) {
        roomRepository.updateFirstOrderById(tableNum, firstOrder)
    }

    //UI함수에서 호출
    fun changeFirstOrder(firstOrder: Int){
        uiState = uiState.copy(
            firstOrder = firstOrder
        )
    }

    fun setFirstOrder(tableNum: Int){
        viewModelScope.launch {
            uiState = uiState.copy(
                firstOrder = roomRepository.getFirstOrder(tableNum).first()
            )
        }
    }



    //UI함수에서 호출
    fun updatePrice(tableNum: Int, price: Int) {
        roomRepository.updatePriceById(tableNum, price)
    }

    fun updateTableNum(tableNum: Int) {
        uiState = uiState.copy(
            tableNum = (tableNum + 1).toString()
        )
    }
    
    //테이블 수를 조절할 경우 uiState를 초기화
    fun updateTableCount(tableCount: String) {
        viewModelScope.launch {
            uiState = TableUiState(tableCount = tableCount)
        }
    }

    private fun getTableCount(): Int {
        return tableListUiState.value.size
    }

    fun insertOrDeleteTable(isInsert: Boolean, tableNum: Int) {
        viewModelScope.launch {
            if (isInsert) {
                for (i in getTableCount() + 1..tableNum) {
                    roomRepository.insertItem(
                        Table(
                            id = 0,
                            firstOrder = 0,
                            tableNum = i,
                            price = "0"
                        )
                    )
                }
            } else withContext(Dispatchers.IO) {
                roomRepository.deleteItemByTableNum(tableNum)
            }
            uiState = uiState.copy(tableCount = "")
        }
    }

    fun isNullOrIntTableCount(): Int {
        if (uiState.tableCount.isEmpty()) return tableListUiState.value.size
        return uiState.tableCount.toInt()
    }

    fun deleteAll(){
        roomRepository.deleteAll()
    }
}

data class TableUiState(
    val tableNum: String? = null,
    val firstOrder : Int = 0,
    val tableCount: String = "",
)
