package com.example.weatherapp.data

import com.example.weatherapp.viewmodel.Language
import com.example.weatherapp.viewmodel.LocationMethod
import com.example.weatherapp.viewmodel.Settings
import com.example.weatherapp.viewmodel.TemperatureUnit
import com.example.weatherapp.viewmodel.WindSpeedUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

interface SettingsRepository {
    //    fun getSettings(): Settings
//    fun saveSettings(settings: Settings)
    fun getLocationMethod(): LocationMethod
    fun getTemperatureUnit(): String
    fun getWindSpeedUnit(): String
    fun getLanguage(): String
    fun updateLocationMethod(method: LocationMethod)
    fun updateTemperatureUnit(unit: TemperatureUnit)
    fun updateWindSpeedUnit(unit: WindSpeedUnit)
    fun updateLanguage(language: Language)
    val settingsFlow: StateFlow<Settings>


}