package com.example.app_pos_compose.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu")
data class Menu(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    var name : String,
    var price : String,
)