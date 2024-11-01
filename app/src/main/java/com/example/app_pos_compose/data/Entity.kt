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

@Entity(tableName = "table")
data class Table(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var tableNum : Int,
    var price : String
)

@Entity(tableName = "order")
data class Order (
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    //val tableId : Int,
    val menu : String,
    val price : String,
)