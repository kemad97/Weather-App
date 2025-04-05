package com.example.weatherapp.model.local

import android.content.SharedPreferences
import com.example.weatherapp.settings.Language
import com.example.weatherapp.settings.LocationMethod
import com.example.weatherapp.settings.Settings
import com.example.weatherapp.settings.TemperatureUnit
import com.example.weatherapp.settings.WindSpeedUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsRepositoryImpl( val preference: SharedPreferences) : SettingsRepository {

    private val _settingsFlow = MutableStateFlow(
        Settings(
            locationMethod = getLocationMethod(),
            temperatureUnit = TemperatureUnit.valueOf(getTemperatureUnit()),
            windSpeedUnit = WindSpeedUnit.valueOf(getWindSpeedUnit()),
            language = Language.valueOf(getLanguage())
        )
    )



    override val settingsFlow: StateFlow<Settings> = _settingsFlow

    companion object {
        private const val PREF_LOCATION_METHOD = "location_method"
        private const val PREF_TEMPERATURE_UNIT = "temperature_unit"
        private const val PREF_WIND_SPEED_UNIT = "wind_speed_unit"
        private const val PREF_LANGUAGE = "language"
    }



    override fun getLocationMethod(): LocationMethod {
        val methodName = preference.getString(PREF_LOCATION_METHOD, LocationMethod.GPS.name)
        return try {
            LocationMethod.valueOf(methodName ?: LocationMethod.GPS.name)
        } catch (e: IllegalArgumentException) {
            LocationMethod.GPS
        }
    }

    override fun getTemperatureUnit(): String {
        return preference.getString(PREF_TEMPERATURE_UNIT, TemperatureUnit.CELSIUS.name)
            ?: TemperatureUnit.CELSIUS.name    }

    override fun getWindSpeedUnit(): String {
        return preference.getString(PREF_WIND_SPEED_UNIT, WindSpeedUnit.METER_PER_SEC.name)
            ?: WindSpeedUnit.METER_PER_SEC.name
    }

    override fun getLanguage(): String {
        return preference.getString(PREF_LANGUAGE, Language.ENGLISH.name) ?: Language.ENGLISH.name
    }

    override fun updateLocationMethod(method: LocationMethod) {
        preference.edit().putString(PREF_LOCATION_METHOD, method.name).apply()
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
            locationMethod = getLocationMethod(),
            temperatureUnit = TemperatureUnit.valueOf(getTemperatureUnit()),
            windSpeedUnit = WindSpeedUnit.valueOf(getWindSpeedUnit()),
            language = Language.valueOf(getLanguage())
        )
    }
}
