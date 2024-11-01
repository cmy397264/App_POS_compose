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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TableViewModel(private val roomRepository: TableRepository) : ViewModel() {
    val tableListUiState : StateFlow<TableListUiState> =
        roomRepository.getAllItems().map { TableListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = TableListUiState()
            )

    var uiState by mutableStateOf(TableUiState())


    fun updateTableNum(tableNum : Int){
        uiState = TableUiState(selectedTableNum = tableNum)
    }

    fun updateTableCount(tableCount : String){
        uiState = TableUiState(tableCount = tableCount)
    }

    private fun getTableCount() : String {
        return tableListUiState.value.tableList.size.toString()
    }

    fun insertOrDeleteTable(isInsert : Boolean, tableNum : Int){
        viewModelScope.launch {
            if (isInsert) {
                for(i in getTableCount().toInt()+1..tableNum){
                    roomRepository.insertItem(Table(0,i,"0"))
                }
                uiState = uiState.copy(tableCount = "$tableNum")
            }
            //insert문과 달리 삭제를 여러번 진행하기 때문에 실행 시간이 insert보다 길다.
            // UI 쓰레드에서 IO 쓰레드로 옮겨서 실행함.
            else withContext(Dispatchers.IO) {
                roomRepository.deleteItemByTableNum(tableNum)
            }
        }
    }

    fun isNullOrIntTableCount() : Int {
        if(uiState.tableCount.isEmpty()) return tableListUiState.value.tableList.size
        return uiState.tableCount.toInt()
    }
}

data class TableUiState(
    val selectedTableNum : Int? = null,
    var tableCount : String = ""
)

data class TableListUiState(
    val tableList : List<Table> = listOf()
)