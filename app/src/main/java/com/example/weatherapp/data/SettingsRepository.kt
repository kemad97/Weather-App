package com.example.weatherapp.data

import android.content.SharedPreferences
import com.example.weatherapp.settings.Language
import com.example.weatherapp.settings.LocationMethod
import com.example.weatherapp.settings.Settings
import com.example.weatherapp.settings.TemperatureUnit
import com.example.weatherapp.settings.WindSpeedUnit
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    fun getLocationMethod(): LocationMethod
    fun getTemperatureUnit(): String
    fun getWindSpeedUnit(): String
    fun getLanguage(): String
    fun updateLocationMethod(method: LocationMethod)
    fun updateTemperatureUnit(unit: TemperatureUnit)
    fun updateWindSpeedUnit(unit: WindSpeedUnit)
    fun updateLanguage(language: Language)
    val settingsFlow: StateFlow<Settings>

    companion object {
        @Volatile
        private var instance: SettingsRepository? = null

        fun getInstance(preferences: SharedPreferences): SettingsRepository {
            return instance ?: synchronized(this) {
                instance ?: SettingsRepositoryImpl(preferences).also { instance = it }
            }
        }
    }

    }