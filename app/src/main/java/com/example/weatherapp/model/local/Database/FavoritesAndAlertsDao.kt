package com.example.weatherapp.model.local.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.model.local.AlertEntity
import com.example.weatherapp.model.local.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesAndAlertsDao {

    @Query("SELECT * FROM favorites_table")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)


    @Query("SELECT * FROM alerts_table")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity)

    @Delete
    suspend fun deleteAlert(alert: AlertEntity)
}