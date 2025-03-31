package com.example.weatherapp.data

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