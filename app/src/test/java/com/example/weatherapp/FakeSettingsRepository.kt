package com.example.weatherapp

import com.example.weatherapp.data.SettingsRepository
import com.example.weatherapp.settings.Language
import com.example.weatherapp.settings.Settings
import com.example.weatherapp.settings.TemperatureUnit
import com.example.weatherapp.settings.WindSpeedUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeSettingsRepository : SettingsRepository {
    private var _useGPS = true
    private var _temperatureUnit = TemperatureUnit.CELSIUS.name
    private var _windSpeedUnit = WindSpeedUnit.METER_PER_SEC.name
    private var _language = Language.ENGLISH.name

    private val _settingsFlow = MutableStateFlow(
        Settings(
            useGPS = _useGPS,
            temperatureUnit = TemperatureUnit.CELSIUS,
            windSpeedUnit = WindSpeedUnit.METER_PER_SEC,
            language = Language.ENGLISH
        )
    )

    override val settingsFlow: StateFlow<Settings> = _settingsFlow

    override fun getUseGPS(): Boolean = _useGPS

    override fun getTemperatureUnit(): String = _temperatureUnit

    override fun getWindSpeedUnit(): String = _windSpeedUnit

    override fun getLanguage(): String = _language

    override fun updateUseGPS(useGPS: Boolean) {
        _useGPS = useGPS
        updateSettingsFlow()
    }

    override fun updateTemperatureUnit(unit: TemperatureUnit) {
        _temperatureUnit = unit.name
        updateSettingsFlow()
    }

    override fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        _windSpeedUnit = unit.name
        updateSettingsFlow()
    }

    override fun updateLanguage(language: Language) {
        _language = language.name
        updateSettingsFlow()
    }

    private fun updateSettingsFlow() {
        _settingsFlow.value = Settings(
            useGPS = _useGPS,
            temperatureUnit = TemperatureUnit.valueOf(_temperatureUnit),
            windSpeedUnit = WindSpeedUnit.valueOf(_windSpeedUnit),
            language = Language.valueOf(_language)
        )
    }
}