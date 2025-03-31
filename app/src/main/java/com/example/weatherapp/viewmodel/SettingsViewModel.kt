package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.ResultState
import com.example.weatherapp.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class SettingsViewModel (private val repository: SettingsRepository) : ViewModel() {
    private val _settingsState = MutableStateFlow<ResultState<Settings>>(ResultState.Loading)
    val settingsState = _settingsState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        try {
            val settings = Settings(
                useGPS = repository.getUseGPS(),
                temperatureUnit = TemperatureUnit.valueOf(repository.getTemperatureUnit()),
                windSpeedUnit = WindSpeedUnit.valueOf(repository.getWindSpeedUnit()),
                language = Language.valueOf(repository.getLanguage())
            )
            _settingsState.value = ResultState.Success(settings)
        } catch (e: Exception) {
            _settingsState.value = ResultState.Error(e)
        }
    }

    fun updateLocationMethod(useGPS: Boolean) {
        try {
            repository.updateUseGPS(useGPS)
            updateSettingsState { it.copy(useGPS = useGPS) }
        } catch (e: Exception) {
            _settingsState.value = ResultState.Error(e)
        }
    }

    fun updateTemperatureUnit(unit: TemperatureUnit) {
        try {
            repository.updateTemperatureUnit(unit)
            updateSettingsState { it.copy(temperatureUnit = unit) }
        } catch (e: Exception) {
            _settingsState.value = ResultState.Error(e)
        }
    }

    fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        try {
            repository.updateWindSpeedUnit(unit)
            updateSettingsState { it.copy(windSpeedUnit = unit) }
        } catch (e: Exception) {
            _settingsState.value = ResultState.Error(e)
        }
    }

    fun updateLanguage(language: Language) {
        try {
            repository.updateLanguage(language)
            updateSettingsState { it.copy(language = language) }
        } catch (e: Exception) {
            _settingsState.value = ResultState.Error(e)
        }
    }

    private fun updateSettingsState(update: (Settings) -> Settings) {
        val currentState = _settingsState.value
        if (currentState is ResultState.Success) {
            _settingsState.value = ResultState.Success(update(currentState.data))
        }
    }
}


class SettingsViewModelFactory(private val repository: SettingsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



data class Settings(
    val useGPS: Boolean = true,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.METER_PER_SEC,
    val language: Language = Language.ENGLISH
)

enum class TemperatureUnit {
    CELSIUS, FAHRENHEIT, KELVIN
}

enum class WindSpeedUnit {
    METER_PER_SEC, MILES_PER_HOUR
}

enum class Language {
    ENGLISH, ARABIC
}

