package com.example.weatherapp.data

import com.example.weatherapp.viewmodel.Language
import com.example.weatherapp.viewmodel.TemperatureUnit
import com.example.weatherapp.viewmodel.WindSpeedUnit

interface SettingsRepository {
//    fun getSettings(): Settings
//    fun saveSettings(settings: Settings)
    fun getUseGPS(): Boolean
    fun getTemperatureUnit(): String
    fun getWindSpeedUnit(): String
    fun getLanguage(): String
    fun updateUseGPS(useGPS: Boolean)
    fun updateTemperatureUnit(unit: TemperatureUnit)
    fun updateWindSpeedUnit(unit: WindSpeedUnit)
    fun updateLanguage(language: Language)
}