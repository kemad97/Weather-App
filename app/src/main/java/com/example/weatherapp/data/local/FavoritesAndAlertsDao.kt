package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesAndAlertsDao {

    @Query("SELECT * FROM favorties_table")
    fun getAllFavorites(): Flow<List<FavoriteWeather>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteWeather)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteWeather)


    @Query("SELECT * FROM alerts_table")
    fun getAllAlerts(): Flow<List<WeatherAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: WeatherAlert)

    @Delete
    suspend fun deleteAlert(alert: WeatherAlert)
}