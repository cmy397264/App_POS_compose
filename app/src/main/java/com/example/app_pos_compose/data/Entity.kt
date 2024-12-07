package com.example.app_pos_compose.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu")
data class Menu(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    var name : String = "",
    var price : String = "",
)

@Entity(tableName = "table")
data class Table(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var firstOrder : Int? = null,
    var tableNum : Int,
    var price : String
)

@Entity(tableName = "order")
data class Order (
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val parentId : Int? = id,
    val orderTable : String,
    val menu : String,
    val price : Int,
    var quantity : Int,
    val orderTime : String,
    val isDone : Boolean = false
)