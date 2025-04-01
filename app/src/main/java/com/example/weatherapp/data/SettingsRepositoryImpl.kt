package com.example.weatherapp.data

import android.content.SharedPreferences
import androidx.work.impl.model.Preference
import com.example.weatherapp.viewmodel.Language
import com.example.weatherapp.viewmodel.Settings
import com.example.weatherapp.viewmodel.TemperatureUnit
import com.example.weatherapp.viewmodel.WindSpeedUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsRepositoryImpl(private val preference: SharedPreferences) : SettingsRepository {

    private val _settingsFlow = MutableStateFlow(
        Settings(
            useGPS = getUseGPS(),
            temperatureUnit = TemperatureUnit.valueOf(getTemperatureUnit()),
            windSpeedUnit = WindSpeedUnit.valueOf(getWindSpeedUnit()),
            language = Language.valueOf(getLanguage())
        )
    )

    override val settingsFlow: StateFlow<Settings> = _settingsFlow

    companion object {
        private const val PREF_USE_GPS = "use_gps"
        private const val PREF_TEMPERATURE_UNIT = "temperature_unit"
        private const val PREF_WIND_SPEED_UNIT = "wind_speed_unit"
        private const val PREF_LANGUAGE = "language"
    }

    override fun getUseGPS(): Boolean {

        return preference.getBoolean(PREF_USE_GPS, true)
    }

    override fun getTemperatureUnit(): String {
        return preference.getString(PREF_TEMPERATURE_UNIT, TemperatureUnit.CELSIUS.name)
            ?: TemperatureUnit.CELSIUS.name
    }

    override fun getWindSpeedUnit(): String {
        return preference.getString(PREF_WIND_SPEED_UNIT, WindSpeedUnit.METER_PER_SEC.name)
            ?: WindSpeedUnit.METER_PER_SEC.name
    }

    override fun getLanguage(): String {
        return preference.getString(PREF_LANGUAGE, Language.ENGLISH.name) ?: Language.ENGLISH.name
    }

    override fun updateUseGPS(useGPS: Boolean) {
        preference.edit().putBoolean(PREF_USE_GPS, useGPS).apply()
        emitNewSettings()

    }

    override fun updateTemperatureUnit(unit: TemperatureUnit) {
        preference.edit().putString(PREF_TEMPERATURE_UNIT, unit.name).apply()
        emitNewSettings()

    }

    override fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        preference.edit().putString(PREF_WIND_SPEED_UNIT, unit.name).apply()
        emitNewSettings()


    }

    override fun updateLanguage(language: Language) {
        preference.edit().putString(PREF_LANGUAGE, language.name).apply()
        emitNewSettings()


    }


    private fun emitNewSettings() {
        _settingsFlow.value = Settings(
            useGPS = getUseGPS(),
            temperatureUnit = TemperatureUnit.valueOf(getTemperatureUnit()),
            windSpeedUnit = WindSpeedUnit.valueOf(getWindSpeedUnit()),
            language = Language.valueOf(getLanguage())
        )
    }
}
