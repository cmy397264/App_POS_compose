package com.example.app_pos_compose.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {
    @Insert
    suspend fun insertMenu(menu: Menu)

    @Update
    suspend fun updateMenu(menu: Menu)

    @Delete
    suspend fun deleteMenu(menu: Menu)

    @Query("SELECT * FROM menu")
    fun getAllMenu(): Flow<List<Menu>>
}