package com.example.weatherapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.data.local.FavoriteDao
import com.example.weatherapp.data.local.FavoriteEntity

@Database(entities = [FavoriteEntity::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    WeatherDatabase::class.java,
                    "weather_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}