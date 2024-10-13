package com.example.app_pos_compose.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Menu::class], version = 1)
abstract class MenuDatabase : RoomDatabase() {
    abstract fun menudao(): MenuDao

    companion object{
        private var Instance :MenuDatabase? = null
        fun getDatabase(context : Context) : MenuDatabase {
            return Instance ?: synchronized(this){
                Room.databaseBuilder(
                    context.applicationContext,
                    MenuDatabase::class.java,
                    "menu_database"
                )
                    .build()
                    .also {
                        Instance = it
                    }
            }
        }
    }
}