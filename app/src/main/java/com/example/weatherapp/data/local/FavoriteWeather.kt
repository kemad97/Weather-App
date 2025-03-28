package com.example.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorties_table")
data class FavoriteWeather (
    @PrimaryKey (autoGenerate = true)
    val id: Int = 0,
    val cityName: String,
    val lat: Double,
    val lon: Double


)
