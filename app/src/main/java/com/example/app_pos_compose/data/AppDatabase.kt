package com.example.app_pos_compose.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Menu::class, Table::class, Order::class], version = 12, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun menuDao(): MenuDao
    abstract fun tableDao(): TableDao
    abstract fun orderDao() : OrderDao

    companion object{
        private var AppDB : AppDatabase? = null

        fun getDatabase(context : Context) : AppDatabase {
            return AppDB ?: synchronized(this){
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Test_Database"
                ).fallbackToDestructiveMigration()
                    .build()
                    .also {
                       AppDB = it
                    }
            }
        }
    }
}