package com.example.weatherapp.data

import android.content.SharedPreferences
import androidx.work.impl.model.Preference

class SettingsRepositoryImpl(private val preference: SharedPreferences) : SettingsRepository {


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
        return preference.edit().putBoolean(PREF_USE_GPS, useGPS).apply()
    }

    override fun updateTemperatureUnit(unit: TemperatureUnit) {
        return preference.edit().putString(PREF_TEMPERATURE_UNIT, unit.name).apply()
    }

    override fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        return preference.edit().putString(PREF_WIND_SPEED_UNIT, unit.name).apply()

    }

    override fun updateLanguage(language: Language) {
        return preference.edit().putString(PREF_LANGUAGE, language.name).apply()

    }


}
